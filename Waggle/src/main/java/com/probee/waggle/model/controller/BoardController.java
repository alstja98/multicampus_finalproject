package com.probee.waggle.model.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.probee.waggle.model.dto.HomeDto;
import com.probee.waggle.model.dto.RequestDto2;
import com.probee.waggle.model.dto.ResultDto;
import com.probee.waggle.model.dto.UserRatingDto;
import com.probee.waggle.model.dto.UsersDto;
import com.probee.waggle.model.dto.VolunteerDto;
import com.probee.waggle.model.service.BoardService;
import com.probee.waggle.model.service.HomeService;
import com.probee.waggle.model.service.VolunteerService;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	VolunteerService volunteerService;
	
	@GetMapping("/list")
	public String selectList(Model model, int ...num) {
		List<RequestDto2> list = boardService.selectList();
		List<String> res_list = new ArrayList<String>();
		Gson gson = new Gson();
		
		int current_page;
		
		try {
			current_page = num[0];
		} catch (Exception e) {
			current_page = 1;
		}
		
		for(int i=0; i<list.size(); i++) {
			String tmp = gson.toJson(list.get(i));
			res_list.add(tmp);
		}
		
		model.addAttribute("List",res_list);
		model.addAttribute("Current_page",current_page);
		
		return "board";
	}
	
	@GetMapping("/requestform")
	public String goRequestForm(Model model, HttpSession session) {
		int id = 0;
		try {
			id = (int)session.getAttribute("user_Code");
			model.addAttribute("user_Code",id);
			return "requestForm";
		} catch (Exception e) {
			return "redirect:/board/list";
		}

	}
	
	@PostMapping("/request")
	public String createRequest(RequestDto2 req_dto, HomeDto home_dto) {
		
		HomeDto find_home = homeService.findHome(home_dto);
		
		if(find_home == null) {
			homeService.insertHome(home_dto);
			find_home = homeService.findHome(home_dto);
		}
		
		req_dto.setReq_HCode(find_home.getHome_Code()); 
		int res = boardService.insertRequest(req_dto);
		
		if(res == 0) {
			System.out.println("not saved...");
			return "redirect:/board/requestform";
		}
		
		System.out.println("success saved!");
		
		// ?????? url?????? ?????? crawling 
		String img_url = boardService.crawlImgUrl(req_dto.getReq_Link());
		
		// DB ??????, req_FCode return
		if(img_url != null) {
			System.out.println("img_url ??????!");

			int req_num = boardService.selectLastRequestNo();
			
			String path = "/images/request/home"+find_home.getHome_Code()+"_"+req_num+".jpg";
			
			int res_savecode = boardService.saveImg(img_url, path);
			
			if(res_savecode != 0) {
				// update req
				int res2 = boardService.updateFCode(req_num,res_savecode);
				if(res2>0) {
					System.out.println("success update!");
				} else {
					System.out.println("fail update...");
				}
			}
		}
		return "redirect:/board/list";
		
	}
	
	@GetMapping("/updateform")
	public String goUpdateForm(Model model, int req_No) {
		model.addAttribute("req_dto", boardService.selectRequest(req_No));
		return "updateForm";
	}
	
	@PostMapping("/update")
	public String updateRequest(RequestDto2 req_dto, HomeDto home_dto) {		
		HomeDto find_home = homeService.findHome(home_dto);
		
		if(find_home == null) {
			homeService.insertHome(home_dto);
			find_home = homeService.findHome(home_dto);
		}
		
		req_dto.setReq_HCode(find_home.getHome_Code()); 
		
		int res = boardService.updateRequest(req_dto);
		
		if(res == 0) {
			System.out.println("not saved...");
			return "redirect:/board/detail?req_No="+req_dto.getReq_No();
		}
		
		System.out.println("saved!");
		return "redirect:/board/detail?req_No="+req_dto.getReq_No();
	}
	
	
	@GetMapping("/detail")
	public String goDetailPage(Model model, HttpSession session, HttpServletResponse response, int req_No) {

		int user_Code = 0;
		Object storedValue = session.getAttribute("user_Code");
		if (storedValue instanceof Integer) {
			user_Code = (int) storedValue;
		}
		// ????????? ??????
		RequestDto2 req_dto = boardService.selectRequest(req_No);
		model.addAttribute("req_dto", req_dto);
		// ????????? ??????
		UsersDto user_dto = boardService.selectUser(req_dto.getReq_UCode());
		model.addAttribute("user_dto", user_dto);
		
		int req_UCode = req_dto.getReq_UCode();
		String req_Stat = req_dto.getReq_Stat();

		if(req_Stat.equals("?????????")) {
			if(user_Code==req_UCode) {
				
				model.addAttribute("vol", boardService.FindVol(req_No));
				return "detail/detail_11";	
			} else {
				VolunteerDto vols = volunteerService.SelectOne(req_No, user_Code);
				
				if(vols != null) {
					session.setAttribute("vo_UCode", vols.getVo_UCode());
					session.setAttribute("vo_Block", vols.getVo_Block());
				} else {
					session.setAttribute("vo_UCode", -1);
					
				}
				
				return "detail/detail_12";
			}
			
		} else if(req_Stat.equals("??????") || req_Stat.equals("??????(0)")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out;

			try {
				out = response.getWriter();
				out.println("<script language='javascript'>");
				out.println("alert('????????? ????????????.')");
				out.println("history.back()");
				out.println("</script>");
				out.flush();
				return null;
				
			} catch (IOException e) {
				e.printStackTrace();
				return "redirect:/board/list";
			}
			
		} else {
			// ?????????, ?????? ????????? ???????????? ????????? Result ?????????????????? ???????????? null??? ??????
			ResultDto res_dto = boardService.selectResult(req_dto.getReq_No());
			int bee_Code = res_dto.getRes_UCode();

			String who = null;
			if(user_Code==req_UCode) {
				who = "?????????";
			} else if(user_Code==bee_Code) {
				who = "?????????";
			} else {
				who = "???3???";
			}
			
			model.addAttribute("who", who);
			
			ResultDto result = boardService.selectResult(req_No);
			Gson gson = new Gson();
			
			model.addAttribute("res_dto", gson.toJson(result));
			model.addAttribute("res_dto2", result);
			System.out.println(result);
			
			if(req_Stat.equals("?????????")) {
				
				return "detail/detail_21";
			} 
			
			
			// result ?????? file
			model.addAttribute("file", boardService.selectResultFile(result.getRes_Code()));
			if(req_Stat.equals("?????????")) {
				if(who.equals("?????????")) {
					return "detail/detail_31";
				} else if(who.equals("?????????")) {
					return "detail/detail_32";
				} else {
					return "detail/detail_21";
				}
				
			} else { //??????
				// ??? ????????? ?????? ???????????? ????????? json?????? ???????????? ??????
				List<UserRatingDto> list = boardService.selectUserRating(req_No);
				List<String> rate_list = new ArrayList<String>();
				
				for(int i=0; i<list.size(); i++) {
					String tmp = gson.toJson(list.get(i));
					rate_list.add(tmp);
				}
				model.addAttribute("user_rating", rate_list);
				
				return "detail/detail_41";
			}			
		}
	}
	
	
	@GetMapping("/accept")  // ????????????
	public String Accept(int req_UCode, int res_UCode, int req_No) {
		
		int res =  boardService.CreateRes(req_No, res_UCode);
		
		if(res >0 ) {
			boardService.Progress(req_No);
		}
		
		return "redirect:/board/detail?req_No=" +req_No;
	}
	

	@GetMapping("/cancel") // ????????? ?????? ???????????? ????????????
	public String Cancel(int req_No) {
		
		volunteerService.delete(req_No);
		boardService.Cancel(req_No);
		
		
		return "redirect:/board/list";
	}
	
	@GetMapping("/revoke") // ????????? ?????? ???????????? ????????????
	public String Revoke(int req_No) {
		
		boardService.Revoke(req_No); // ????????? ??????(0)
		volunteerService.delete(req_No); // ???????????? ??????
		volunteerService.Revoke(req_No); // ????????? ??????(0)
		
		return "redirect:/board/list";
	}
	

	
}
