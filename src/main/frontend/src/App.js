import { Routes, Route } from "react-router-dom";
import "./App.css";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Board from "./pages/Board";
import BoardWrite from "./pages/BoardWrite";
import BoardDetail from "./pages/BoardDetail";
import { useEffect, useState } from "react";
import api from "./api/axiosConfig";

function App() {
  const [user, setUser] = useState(null); // 현재 로그인한 유저의 아이디(username)
  const checkUser = async () => {
    try {
      const res = await api.get("/api/auth/me");
      setUser(res.data.username);
    } catch (error) {
      setUser(null);
    }
  };
  useEffect(() => {
    checkUser();
  }, []);

  // 로그아웃
  const handleLogout = async () => {
    await api.post("/api/auth/logout");
    setUser(null);
    alert("성공적으로 로그아웃 되었습니다.");
  };

  return (
    <div className="App">
      <Navbar onLogout={handleLogout} user={user} />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login onLogin={setUser} />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/board" element={<Board user={user} />} />
        <Route path="/board/write" element={<BoardWrite user={user} />} />
        <Route path="/board/:id" element={<BoardDetail user={user} />} />
      </Routes>
    </div>
  );
}

export default App;
