package nifi.worklow.service;

import org.springframework.stereotype.Service;


@Service
public class InputPortService extends PortService {

	
	public InputPortService() {
		super();
	}
	
	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
		this.serviceUrl = baseUrl.endsWith("/")? (baseUrl + "input-ports/") : (baseUrl +"/input-ports/");
	}

}
