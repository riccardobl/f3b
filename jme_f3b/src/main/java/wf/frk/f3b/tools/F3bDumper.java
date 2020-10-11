package wf.frk.f3b.tools;

import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.protobuf.TextFormat;

import f3b.Datas.Data;



// Dump f3b(=protobuf) to an human readable format file.
public class F3bDumper{
	static void usage() {out.println("Usage:\n -f input [-o output] ");}

	public static void main(String[] args) throws IOException {

		if(args.length==0){//Interactive
			usage();
			Scanner in=new Scanner(System.in);
			out.print("Args: ");
			args=in.nextLine().split("\\s+");
			in.close();
		}



		String file=findArg(args,"f");
		if(file==null){usage();exit(1);}

		out.printf("Read from %s\n",file);
		BufferedInputStream bi=new BufferedInputStream(new FileInputStream(file));
		Data data=Data.parseFrom(bi);
		bi.close();

		String human_data=TextFormat.printToString(data);
		
		String out_file=findArg(args,"o");
		if(out_file==null) out.println(human_data);
		else{
			out.printf("Write to %s\n",out_file);
			FileOutputStream fos=new FileOutputStream(out_file);
			fos.write(human_data.getBytes(Charset.forName("UTF-8")));
			fos.close();
		}
	}

	private static String findArg(String[] args, String find) {
		for (int i=0;i<args.length;i++){			
			if(i>0&&args[i-1].toLowerCase().equals("-"+find)){
				return args[i];
			}
		}
		return null;
	}
}
