/*

SiteAlert, what are you waiting for?

Copyright (c) 2013, Matteo Pietro Dazzi <---> ilteoood
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the
  following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
  the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

========================================================================================================================
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
//IMPORT FOR THE MAIL
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SiteAlert
{
  @SuppressWarnings("empty-statement")
  public static void main(String[] args) throws IOException, InterruptedException
  {
    final String separator=separator();
    String s,path;
    String[] dirs = findDirs(separator);
    int length=dirs.length;
    Scanner sc = new Scanner(System.in);
    File f;
    int x = choice();
    while(x<6)
    {
        clearScreen();
        switch(x)
        {
            case 1:
                displaySites(x,dirs,length);
                break;
            case 2:
                addSite(null, null,null,separator);
                break;
            case 3:
		length=dirs.length;             
                if(length!=0)
		{
                    System.out.println("Write the number of the site that you want to fetch.");
                    s=displaySites(x,dirs,length);
                    if(s!=null)
                    {
                        path=findHome()+separator+"SiteAlert"+separator+s+separator+"sito.txt";
                        f=new File(path);
                        if(f.exists())
                        {
                            BufferedReader br=new BufferedReader(new FileReader(f));
                            String site=br.readLine();
                            path=br.readLine();
                            addSite(site,s,path,separator);
                        }
                        else
                            System.out.println("No configuration file found.");
                    }
                }
                else
                    System.out.println("You haven't any site!");
                break;
            case 4:
                System.out.println("Do you want to check continually? (Y/n)");
                while ((s=sc.nextLine()).length()==0 || ((s.charAt(0) != 'n') && (s.charAt(0) != 'y')))
                {
                    if(s.length()==0)
                    {
                        s="y";
                        break;
                    }
                    else
                        System.out.println("Wrong input, do you want to check continually? (Y/n)");
                }
                while(true)
                {
                    if(checkSite(separator," ") || !s.equals("y"))
                    {
                                s="n";
                                break;
                    }
                    else
                        Thread.sleep(30000);
                }
                break;
            case 5:
                if(length!=0)
                {
                    System.out.println("Write the number of the site that you want to delete.");
                    s=displaySites(x,dirs,length);
                    if(s!=null)
                    {
                        path=findHome()+separator+"SiteAlert"+separator+s;
                        f=new File(path);
                        if(f.delete())
                            System.out.println("Site successfully deleted!");
                        else
                        {
                            String[] content=f.list();
                            for(String st:content)
                            {
                                f=new File(path+separator+st);
                                f.delete();
                            }
                            f=new File(path);
                            if(f.delete())
                                System.out.println("Site successfully deleted!");
                            else
                                System.out.println("Something went wrong");
                        }
                    }
                }
                else
                    System.out.println("You haven't checked any site!");
                break;
        }
        waitUser();
        x = choice();
    }
    clearScreen();
  }
  public static void clearScreen() throws IOException
  {
    if (!System.getProperty("os.name").contains("Windows"))
    {
        Process p=Runtime.getRuntime().exec("clear");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        System.out.println(br.readLine());
    }
    else
        for(int i=0;i<100;i++)
            System.out.println();
  }
  public static int choice() throws IOException 
  {
    Scanner sc = new Scanner(System.in);
    visualizeMenu();
    try
    {
      int x;
      while ((x = sc.nextInt()) > 6)
        if (x != 6)
          visualizeMenu();
      return x;
    }
    catch (IOException e)
    {
      clearScreen();
      System.out.println("Wrong input");
      return 5;
    }
  }
  public static void visualizeMenu() throws IOException
  {
      clearScreen();
      System.out.println("What do you want to do?\n1) Display sites\n2) Add new site to check\n3) Fetch a site from the config file\n4) Check sites\n5) Delete a site\n6) Exit");
  }
  public static String findHome() throws IOException 
  {
    return System.getProperty("user.home");
  }
  public static String[] findDirs(String s) throws IOException
  {
        String path = findHome() + s+"SiteAlert"+s;
        File f = new File(path);
        if(!f.exists())
            f.mkdir();
        String[] dirs = f.list();
        return dirs;
  }
  public static String displaySites(int x, String[] dirs, int length)
  {
      if(x!=1)
          System.out.println("0) Exit");
      int i=0;
      for(i=0;i<length;i++)
          System.out.println((i+1)+") "+dirs[i]);
      if(x!=1)
      {
        Scanner sc=new Scanner(System.in);
        do
        {
            System.out.print("Number of the site: ");
            try
            {
                i=sc.nextInt();
            }
            catch(InputMismatchException e)
            {
                sc=new Scanner(System.in);
                i=0;
            }
        }
        while(i<0 || i>length);
        return (i==0?null:dirs[i-1]);
      }
      return null;
  }
  public static void addSite(String site, String nameSite,String mail,String s) throws IOException 
  {
    String sito = site, nomeSito = nameSite, email=mail;
    if ((sito == null) || (nomeSito == null) || (email==null))
    {
      Scanner sc = new Scanner(System.in);
      System.out.println("Insert the link for the site: ");
      sc = new Scanner(System.in);
      sito = sc.nextLine();
      System.out.println("Insert a name for the site: ");
      nomeSito = sc.nextLine();
      System.out.println("Insert the email where you want to be informed: (if you want to add other mail, separate them with \";\")");
      email = sc.nextLine();
    }
    try
    {
      URL urli = new URL(sito);
      HttpURLConnection http = (HttpURLConnection)urli.openConnection();
      http.setUseCaches(false);
      int risposta = http.getResponseCode();
      if (risposta == HttpURLConnection.HTTP_NOT_FOUND)
      {
        System.out.println("This page doesn't exist!");
      }
      else if (risposta == HttpURLConnection.HTTP_OK)
      {
        String path = findHome() + s+"SiteAlert"+s + nomeSito;
        File f = new File(path);
        if (!f.isDirectory())
        {
          if (!f.mkdirs())
            System.out.println("I can't create the necessary directory!");
          else
            saveFile(path + s+"sito.txt",sito,email,http);
        }
        else if(site ==null && nameSite==null)
            System.out.println("Name already used!");
        else
            saveFile(path + s+"sito.txt",sito,email,http);
      }
    }
    catch (MalformedURLException e)
    {
      System.out.println("There is an error with the link!");
    }
  }
  public static boolean checkSite(String separator,String s) throws IOException
  {
      String[] dirs=findDirs(separator);
      String path = findHome() + separator+"SiteAlert"+separator;
      Scanner sc=new Scanner(System.in);
      BufferedReader br;
      File f;
      URL urli = null;
      HttpURLConnection http = null;
      if(dirs.length!=0)
      {
          for (String dir : dirs)
          {
              String pathModificata = path + dir + separator+"sito.txt";
              try
              {
                  f = new File(pathModificata);
                  br = new BufferedReader(new FileReader(f));
                  String sito = br.readLine(),mail=br.readLine(),contenutoLocale;
                  StringBuffer lc=new StringBuffer();
                  while((contenutoLocale = br.readLine())!=null)
                      lc.append(contenutoLocale);
                  contenutoLocale=lc.toString();
                  urli = new URL(sito);
                  http = (HttpURLConnection)urli.openConnection();
                  br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                  String contenutoRemoto;
                  StringBuffer rc=new StringBuffer();
                  while((contenutoRemoto = br.readLine())!=null)
                      rc.append(contenutoRemoto);
                  contenutoRemoto=rc.toString();
                  if (contenutoLocale.equals(contenutoRemoto))
                      System.out.println("The site \"" + dir + "\" hasn't been changed!");
                  else 
                  {
                      System.out.println("The site \"" + dir + "\" has been changed!");
                      if(s.equals(""))
                      {
                          System.out.println("Do you want to update your local file? (Y/n)");
                          while ((contenutoLocale=sc.nextLine()).length()==0 || ((contenutoLocale.charAt(0) != 'n') && (contenutoLocale.charAt(0) != 'y')))
                          {
                              if(contenutoLocale.length()==0)
                              {
                                  contenutoLocale="y";
                                  break;
                              }
                              else
                                  System.out.println("Wrong input, do you want to update the local file? (Y/n)");
                          }
                          char c = contenutoLocale.charAt(0);
                          if (c == 'y')
                              addSite(sito, dir,mail,separator);
                      }
                      else
                      {
                          addSite(sito, dir,mail,separator);
                          try
                          {sendMail(mail,dir,sito);}
                          catch(AddressException e)
                          {
                              System.out.println("Error with the e-mail destination address.");
                          }
                      }
                  }
              }
              catch (IOException e)
              {
                  pathModificata = path + dir;
                  f = new File(pathModificata);
                  f.delete();
              }
          }
      }
      else
      {
          System.out.println("You haven't checked any site!");
          return true;
      }
      return false;
  }
  public static void saveFile(String path,String sito,String mail,HttpURLConnection http) throws IOException
  {
            File f = new File(path);
            if (f.exists())
                f.delete();
            FileWriter fw = new FileWriter(f);
            fw.write(sito + "\n");
            fw.write(mail+"\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            while ((sito = br.readLine()) != null)
                fw.write(sito + "\n");
            fw.flush();
            fw.close();
            System.out.println("Site saved correctly!");
  }
  public static String separator()
  {
      if(System.getProperty("os.name").contains("Windows"))
          return "\\";
      else
          return "/";
  }
  public static void sendMail(String to, String subject, String sito) throws AddressException
  {
      Properties p = new Properties();
      p.put("mail.smtp.host", "smtp.gmail.com");
      p.put("mail.smtp.port", "587");
      p.put("mail.smtp.auth","true");
      p.put("mail.smtp.starttls.enable", "true");
      Authenticator authenticator = new Authenticator () 
      {
	public PasswordAuthentication getPasswordAuthentication()
        {
		return new PasswordAuthentication("SiteAlertMailNotification@gmail.com" ,"SiteAlertMailNotificatio");
	}
      };
      Session s = Session.getDefaultInstance(p , authenticator); 
      Message m = new MimeMessage(s);
      try 
      {
          m.setFrom(new InternetAddress("SiteAlertMailNotification@gmail.com"));
          String[] toArray=to.split(";");
          Address[] a=new Address[toArray.length];
          int i=0;
          for(String toSingle:toArray)
              a[i++]=new InternetAddress(toSingle);
          m.setRecipients(RecipientType.BCC, a);
          m.setSubject("The site \""+subject+"\" has been changed!");
          m.setText("The site \""+subject+"\" has been changed!\nLink: "+sito);
          Transport.send(m);
      }
      catch (MessagingException e) {}
  }
  public static void waitUser()
  {
      Scanner sc=new Scanner(System.in);
      System.out.println("Press enter to continue...");
      sc.nextLine();
  }
}