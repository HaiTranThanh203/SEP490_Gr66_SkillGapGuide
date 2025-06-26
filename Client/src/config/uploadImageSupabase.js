import {supabase} from "./supabase"; // Ensure correct relative path

const BUCKET_NAME = 'fpt-image';

export const  uploadImageToSupabase = async (file) => {
  if (!file) throw new Error("No file provided");

  const fileExt = file.name.split('.').pop(); // Lấy phần mở rộng của file
  const fileName = `${Date.now()}.${fileExt}`; // Tạo tên file duy nhất
  const filePath = `${fileName}`; // Đường dẫn file trong bucket

  console.log("📸 Uploading file to:", filePath);

  // Upload the file to the bucket
  const { data, error } = await supabase.storage
    .from(BUCKET_NAME)
    .upload(filePath, file, {
      cacheControl: '3600',
      upsert: false
    });

  if (error) {
    console.error("❌ Upload error:", error);
    throw error;
  }

  // Lấy public URL của file vừa upload
  const { data: publicData } = supabase
    .storage
    .from(BUCKET_NAME)
    .getPublicUrl(filePath);

  console.log("✅ Public URL:", publicData.publicUrl);
  return publicData.publicUrl;
};
