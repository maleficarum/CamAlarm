import java.net.Socket;
import java.io.BufferedWriter


Socket s = new Socket("localhost",9090);
BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
wr.write("aString\n");
wr.flush();

s.close();