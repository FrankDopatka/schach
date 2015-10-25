import java.io.IOException;

import ki.*;
import frontend.Frontend;

public class StartClientKI {
	public static void main(String[] args) throws IOException{
		new Frontend(Konstanten.zumServer,false,new KI002()); // false: schwarz
	}
}
