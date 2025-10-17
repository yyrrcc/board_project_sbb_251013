package com.mycompany.sbbpjboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
   
	// 부가설명->컨트롤러 가 없으면 리액트 라우터 요청이 동작 안함
	//api요청->스프링 부트 요청 적용
	//api요청을 제외한 요청 React 라우터 요청으로 변경->리액트의 "/"로 시작하는 요청으로 변경
	@RequestMapping(value = {"/{path:^(?!api$|static|index\\.html$).*$}/**" })
    public String forward() {
        return "forward:/index.html";
    }

}