package nifi.worklow.service;

import org.springframework.stereotype.Service;


@Service
public class OutPutPortService extends PortService {

	public OutPutPortService() {
		super();
	}
	
	@Override
	public void init(String baseUrl) {
		super.init(baseUrl);
		this.serviceUrl = baseUrl.endsWith("/")? (baseUrl + "output-ports/") : (baseUrl +"/output-ports/");
	}
}
