package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Entity.*;
import com.skillgapguide.sgg.Repository.JobCvSkillScoreRepository;
import com.skillgapguide.sgg.Repository.JobDesSkillsRepository;
import com.skillgapguide.sgg.Repository.UserCvSkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CosineSimilarityService {
    @Autowired
    private EmbedService embedService;
    @Autowired
    private JobCvSkillScoreRepository jobCvSkillScoreRepository;
    @Autowired
    private JobDesSkillsRepository jobDesSkillsRepository;
    @Autowired
    private UserCvSkillsRepository userCvSkillsRepository;
    public void compareCvJob(int jobId, int cvId) throws Exception {
        if(jobCvSkillScoreRepository.findByCvSkillAndJobSkill(cvId, jobId) != null){

        }
        jobCvSkillScoreRepository.deleteByJobIdAndCvId(jobId,cvId);
        List<UserCvSkills> userCvSkillsList = userCvSkillsRepository.findByCvId(cvId);
        List<JobDesSkills> jobDesSkillsList = jobDesSkillsRepository.findByJobId(jobId);
        int m = userCvSkillsList.size();
        int n = jobDesSkillsList.size();
        int size = Math.max(m, n);              // square dimension

        // 1. Build similarity matrix with padding.
        double[][] similarity = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < m && j < n) {
                    similarity[i][j] = getCosine(userCvSkillsList.get(i), jobDesSkillsList.get(j));
                } else {
                    similarity[i][j] = 0.0;   // dummy row or column
                }
            }
        }

        // 2. Convert to cost matrix (Hungarian minimizes cost).
        double[][] cost = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cost[i][j] = 1.0 - similarity[i][j];
            }
        }

        // 3. Solve assignment.
        HungarianAlgorithm hungarian = new HungarianAlgorithm(cost);
        int[] assignment = hungarian.execute(); // assignment[i] = column chosen for row i (or -1)

        // 4. Persist results.
        Set<Integer> matchedJobIndexes = new HashSet<>();
        for (int cvIdx = 0; cvIdx < assignment.length; cvIdx++) {
            int jobIdx = assignment[cvIdx];
            if (jobIdx == -1) continue;           // no assignment (should not happen in square matrix)

            matchedJobIndexes.add(jobIdx);
            JobCvSkillScore row = new JobCvSkillScore();
            if (cvIdx < m && jobIdx < n) {
                // Real CV skill ↔ real Job skill.
                row.setJobSkill(jobDesSkillsList.get(jobIdx).getId());
                row.setCvSkill(userCvSkillsList.get(cvIdx).getId());
                row.setScore(similarity[cvIdx][jobIdx]);
            } else if (jobIdx < n) {
                // Dummy CV skill ↔ real Job skill → No match.
                row.setJobSkill(jobDesSkillsList.get(jobIdx).getId());
                row.setCvSkill(null);
                row.setScore(0.0);
            } else {
                // Real CV skill ↔ dummy Job skill → CV skill not used. Skip persisting if not needed.
                continue;
            }
            jobCvSkillScoreRepository.save(row);
        }

        // 5. For Job skills left unmatched (possible when m < n).
        for (int j = 0; j < n; j++) {
            if (!matchedJobIndexes.contains(j)) {
                JobCvSkillScore row = new JobCvSkillScore();
                row.setJobSkill(jobDesSkillsList.get(j).getId());
                row.setCvSkill(null);
                row.setScore(0.0);
                jobCvSkillScoreRepository.save(row);
            }
        }
    }

    public double getCosine(UserCvSkills cv, JobDesSkills job) throws Exception {
        double[] vec1 = embedService.getCvSkillEmbedding(cv.getSkill());
        double[] vec2 = embedService.getJobDesSkillEmbedding(job.getSkill());
        double result = cosineSimilarity(vec1,vec2);
        return result;
    }
    public double testCosine(String cv, String job) throws Exception {
        double[] vec1 = embedService.getCvSkillEmbedding(cv);
        double[] vec2 = embedService.getJobDesSkillEmbedding(job);
        double result = cosineSimilarity(vec1,vec2);
        return result;
    }

    /**
     * Calculates the cosine similarity between two vectors a and b.
     * @param a Real number table representing the first vector
     * @param b Real number table representing the second vector
     * @return value between -1 and 1
     */
    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];      // Dot product
            normA += a[i] * a[i];    // Sum of squares for a
            normB += b[i] * b[i];    // Sum of squares for b
        }
        // Cosine similarity = dot product / (magnitude of a * magnitude of b)
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
