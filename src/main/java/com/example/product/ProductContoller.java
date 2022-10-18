package com.example.product;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ProductContoller {
	
	
	@Autowired
	ProductDAO productDao;
	
	@RequestMapping("/")
	public ModelAndView home() {
		return new ModelAndView("index","","");
	}
	
	@RequestMapping("/list")
	public List<Map<String, Object>> list(@RequestParam(defaultValue = "") String product_name) {
		// @RequestParam이 붙은 변수는 get,porst 방식으로 전달 된 값들을 저장하는 용도의 변수
		return productDao.list(product_name);
	}
	
	@RequestMapping("/insert")
	public void insert(@RequestParam Map<String, Object> map, // WriteProduct.js에서 입력한 값들이 Map에 전달 
			@RequestParam(required = false) MultipartFile img, // 첨부 파일은 Map에 저장되지 않으므로 MultipartFile에 별도로 전달 
			HttpServletRequest request) { // 사용자의 요청사항을 처리하는 객체
		String filename= "-"; // 첨부파일이 없는 경우 빈값
		if (img != null && !img.isEmpty()) { // img가 null이 아니고 빈값이 아니라면
			filename = img.getOriginalFilename(); // filname은 ~이다.
			try { // java에서 파일 입출력은 반드시 예외처리를 해야하므로 try~catch문 처리
				ServletContext application = request.getSession().getServletContext(); // 웹서버의 정보를 조회할 수 있는 객체
				String path = application.getRealPath("/static/images/"); // 현재 실행중인 웹프로젝트의 실제 서비스 경로를 구함
				img.transferTo(new File(path + filename));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		map.put("filename", filename);
		productDao.insert(map);
	}
	
	@RequestMapping("/detail/{product_code}")
	public Map<String, Object> detail(@PathVariable String product_code, ModelAndView mav){
		return productDao.detail(product_code);
	}
	
	@RequestMapping("/update")
	public void update(@RequestParam Map<String, Object>map, 
			@RequestParam(required = false) MultipartFile img, 
			HttpServletRequest request) {
		String filename = "-";
		if (img != null && !img.isEmpty()) {
			filename = img.getOriginalFilename();
			try {
				ServletContext application = request.getSession().getServletContext();
				String path = application.getRealPath("/static/images/");
				img.transferTo(new File(path + filename));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String product_code = map.get("product_code").toString();
			Map<String, Object> product = productDao.detail(product_code);
			filename = product.get("filename").toString();
		}
		map.put("filename", filename);
		productDao.update(map);
	}
	
	@RequestMapping("/delete")
	public void delete(int product_code, HttpServletRequest request) {
		String filename = productDao.filename(product_code);
		if (filename != null && !filename.equals("-")) {
			ServletContext application = request.getSession().getServletContext();
			String path = application.getRealPath("/static/images/");
			File file = new File(path + filename);
			if (file.exists()) {
				file.delete();
			}
		}
		productDao.delete(product_code);
	}
	


}
