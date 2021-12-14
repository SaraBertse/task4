package lecture4;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.security.KeyStore;

public class Server{
    
    public static void main(String[] args){
        SSLServerSocketFactory ssf; // = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        
        try{
            KeyStore ks = null;
            ks = KeyStore.getInstance("JKS", "SUN");
            //ks = KeyStore.getInstance("jks");
            InputStream is = null;
            //is = new FileInputStream(new File("C:/Program Files/Java/jdk1.8.0_271/jre/lib/security/cacerts"));
            is = new FileInputStream(new File("C:/Users/sarab/cert.pfx"));
            char[] pwd = "qwerty".toCharArray();
            ks.load(is,pwd);
            
            
            SSLContext ctx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pwd);
            ctx.init(kmf.getKeyManagers(), null, null);
            //ctx.init(null, null, null);
            ssf = ctx.getServerSocketFactory();
            
            
            System.out.println("Supported:");
         //   for(int i = 0; i < ssf.getSupportedCipherSuites().length; i++)
          //      System.out.println(ssf.getSupportedCipherSuites()[i]);
            SSLServerSocket ss = null;
            ss = (SSLServerSocket)ssf.createServerSocket(443);
            //String[] cipher = {"SSL_DH_anon_WITH_RC4_128_MD5"};
            String[] cipher = {"TLS_RSA_WITH_AES_128_CBC_SHA"};
            //ss.setEnabledCipherSuites(cipher);
            //System.out.println("Choosen:");
           // for(int i = 0; i < ss.getEnabledCipherSuites().length; i++)
           //     System.out.println(ss.getEnabledCipherSuites()[i]);
            SSLSocket socket = (SSLSocket)ss.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String row = null;
           /* System.out.println("----------received HTTPS request----------");
            while( ((row=reader.readLine()) != null) || (row=="\\r\\n"))  {
                System.out.println(row);
                System.out.println("stuck spinning");
                if(!(row.trim().equals(""))){ 
                    break;
                }
            }*/
           
        //   char request = 0;
        //   int c = 0;
        //   while ((c = reader.read())!= 38){
       //      System.out.print((char)c);
        //}
            System.out.println("----------before reader closed----------");
              reader.close();
              System.out.println("----------reader closed----------");
            
              SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
        HttpsURLConnection.setDefaultSSLSocketFactory(sf);
        SSLSocket mailSocket = null;
        String host = "webmail.kth.se";
        mailSocket = (SSLSocket)sf.createSocket(host,993);

        //String host = "localhost";
               try{
            mailSocket.startHandshake();
        }
        catch(IOException e){
            System.out.println("*************" + e.getMessage());
        }
               BufferedReader mailReader = null;
               PrintWriter mailWriter = null;
        try{

            mailReader = new BufferedReader(new InputStreamReader(mailSocket.getInputStream()));
            mailWriter = new PrintWriter(mailSocket.getOutputStream());
        }
        catch(MalformedURLException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
            //bertse@ug.kth.se
        System.out.println("----------Starting IMAP-----------------");
          String str;
        str=mailReader.readLine();
             System.out.println(str);
        mailWriter.println("a001 login bertse tempPWForTask4");

      //  mailWriter.println("Host: " + host);
      //  mailWriter.println("");
      //  mailWriter.flush();
      //  System.out.println("HTTP-request sent");
            mailWriter.flush();
                
           // while( (str=mailReader.readLine()) != null)
            //    System.out.println(str);
             str=mailReader.readLine();
             System.out.println(str);
            System.out.println("-----------------moved on--------------");
            
            mailWriter.println("a002 select inbox");
            mailWriter.flush();
            mailWriter.println("a003 fetch 1 full");
             mailWriter.flush();
             str=mailReader.readLine();
             System.out.println(str);
             System.out.println("mail reader is ready 1? " + mailReader.ready());
            while(mailReader.ready()){
                str = mailReader.readLine();
                System.out.println(str);
            }
           
  
            
            mailWriter.println("a003 fetch 1 full");
             mailWriter.flush();
             str=mailReader.readLine();
             System.out.println(str);
             str=mailReader.readLine();
             System.out.println(str);
      
             System.out.println("mail reader is ready 2? " + mailReader.ready());
                        while(mailReader.ready()){
                str = mailReader.readLine();
                System.out.println(str);
            }
            mailWriter.flush();
            mailWriter.close();
            mailReader.close();
            System.out.println("----------Before socket close-----------------");
            mailSocket.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}