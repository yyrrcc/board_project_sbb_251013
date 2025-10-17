import "./Navbar.css";
import { Link } from "react-router-dom";

const Navbar = ({ onLogout, user }) => {
  return (
    <>
      <nav className="navbar">
        <div className="logo">회사 게시판</div>
        <div className="menu">
          {user && <span>{user}님 로그인 중!</span>}
          <Link to="/">Home</Link>
          <Link to="/board">게시판</Link>
          {!user && <Link to="/login">로그인</Link>}
          {!user && <Link to="/signup">회원가입</Link>}
          {user && (
            <button onClick={onLogout} className="logout-btn">
              로그아웃
            </button>
          )}
        </div>
      </nav>
    </>
  );
};
export default Navbar;
