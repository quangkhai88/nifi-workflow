package nifi.worklow;

import nifi.worklow.param.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("nifi.worklow")
public class NifiWorkflowApplication  implements CommandLineRunner, Result{

	
	public static void main(String[] args) {
		SpringApplication nifiWorkflow = new SpringApplication(NifiWorkflowApplication.class);
		nifiWorkflow.run(args);
	}
	
	
	@Autowired
	private NifiWorkflow nifiWorkflow;
	
	
	@Override
	public void run(String... args) {

		System.out.println();
		System.out.println("**********************************************  TPS NIFI WORKFLOW - START  **********************************************");
		
		int codeResult = nifiWorkflow.initWorkflow();
		
		if (codeResult == SUCCESS) {
			codeResult = nifiWorkflow.loadAllNifiProcessors();
		}
		
		if (codeResult == SUCCESS) {
			codeResult = nifiWorkflow.validateNifiWorkFlowConfiguration();
		}
		
		if (codeResult == SUCCESS) {
			codeResult = nifiWorkflow.startNifiWorkflowProcess();
		}
		
		String nifiWorkflowControlToolStatus = codeResult == SUCCESS ? "started" : "not started";
		
		System.out.println("Status : " + (codeResult == SUCCESS ? "OK" : "KO"));
		
		System.out.println("Result : NIFI workflow control tool is " + nifiWorkflowControlToolStatus +", code return :  " + codeResult);
		
		System.out.println("There are " + nifiWorkflow.getUpdatedProcessorNumber() + "/" + nifiWorkflow.getTotalProcessors() + " processors were updated");
		
		System.out.println("**********************************************  TPS NIFI WORKFLOW - END  **********************************************");
		System.out.println();
		
		System.exit(codeResult);
    }
}
