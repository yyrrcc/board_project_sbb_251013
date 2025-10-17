import "./Login.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosConfig.js";

const Login = ({ onLogin }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      // 폼 데이터 방식이라 URLSearchParams로 전송
      await api.post("/api/auth/login", new URLSearchParams({ username, password }));
      // 현재 로그인한 사용자 정보 가져오기
      const res = await api.get("/api/auth/me");
      onLogin(res.data.username); // App 컴포넌트에서 전달된 props 값으로 로그인한 유저의 username 전달
      alert("로그인 성공");
      navigate("/", { replace: true });
    } catch (error) {
      console.error(error);
      alert("로그인 실패");
    }
  };

  return (
    <div className="form-login-container">
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="아이디" />
        <br />
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호" />
        <br />
        <button type="submit">로그인</button>
      </form>
    </div>
  );
};
export default Login;
