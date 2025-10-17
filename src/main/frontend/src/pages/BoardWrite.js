import { useState } from "react";
import "./BoardWrite.css";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";

const BoardWrite = ({ user }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const navigate = useNavigate();

  // **유효성 체크
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault(); // submit동안 다른 이벤트가 발생하지 않도록 중지시키는 것 (새로고침 방지)
    // **유효성 체크
    setErrors({});

    if (!user) {
      alert("로그인한 유저만 작성 가능합니다.");
      return;
    }
    try {
      await api.post("/api/board", { title, content });
      // alert("글 작성 성공");
      navigate("/board");
    } catch (error) {
      // **유효성 체크 에러
      if (error.response && error.response.status === 400) {
        setErrors(error.response.data);
      } else {
        console.error(error);
        alert("글쓰기 실패!");
      }
    }
  };

  return (
    <div className="write-container">
      <h2>글쓰기</h2>
      <form onSubmit={handleSubmit} className="write-form">
        <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="제목" />
        {/* **에러 출력 */}
        {errors.title && <p style={{ color: "red" }}>{errors.title}</p>}

        <textarea value={content} onChange={(e) => setContent(e.target.value)} placeholder="내용" />
        {/* **에러 출력 */}
        {errors.content && <p style={{ color: "red" }}>{errors.content}</p>}

        <div className="button-group">
          <button type="submit">등록</button>
          <button type="button" onClick={() => navigate("/board")}>
            취소
          </button>
        </div>
      </form>
    </div>
  );
};

export default BoardWrite;
