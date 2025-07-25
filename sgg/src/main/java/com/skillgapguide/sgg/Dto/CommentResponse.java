package com.skillgapguide.sgg.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CommentResponse {
    String generalComment;
    List<SkillCommentResponse> skillComment;
}
