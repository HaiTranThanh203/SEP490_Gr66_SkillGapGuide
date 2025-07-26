from FlagEmbedding import BGEM3FlagModel

import json
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

# Nhúng văn bản thành vector
model = BGEM3FlagModel('BAAI/bge-m3', use_fp16=True)
class TextInput(BaseModel):
    text: str

@app.post("/embed")
def get_embedding(input: TextInput):
    output = model.encode([input.text], return_dense=True)
    vector = output['dense_vecs'][0].tolist()
    return {"embedding": vector}

# Chạy server
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
