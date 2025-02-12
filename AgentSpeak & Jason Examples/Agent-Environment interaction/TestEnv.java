import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;

public class TestEnv extends jason.environment.Environment {

  private Logger logger = Logger.getLogger("testenv.mas2j."+TestEnv.class.getName());
  
  /** Called before the MAS execution with the args informed in .mas2j */
  @Override
  public void init(String[] args) {
	  
  }

  @Override
  public synchronized boolean executeAction(String agName, Structure action) {
	  
    if (action.getFunctor().equals("burn")) {
		logger.info("the fire has started and is spreading too much");
		addPercept(Literal.parseLiteral("fire"));
		return true;
	  
    } else if(action.getFunctor().equals("run")) {
		logger.info(agName + " has runned away from the fire");
		return true;
	  
	} else {
		logger.info("action "+action.getFunctor()+" not implemented!");
		return false;
    }
  }

  /** Called before the end of MAS execution */
  @Override
  public void stop() {
	  super.stop();
  }
}