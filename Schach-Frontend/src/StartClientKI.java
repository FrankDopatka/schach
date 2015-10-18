import java.io.IOException;

import ki001.KI001;
import frontend.Frontend;

public class StartClientKI {
	public static void main(String[] args) throws IOException{
		new Frontend(Konstanten.zumServer,false,new KI001()); // false: schwarz
	}
}
