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

TODO list:
- Periodical check
- Default choice

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
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SiteAlert
{
  @SuppressWarnings("empty-statement")
  public static void main(String[] args) throws IOException
  {
    final String separator=separator();
    Scanner sc = new Scanner(System.in);
    int x = choice();
    while(x<4)
    {
        switch(x)
        {
            case 1:
                clearScreen();
                addSite(null, null,null,separator);
                break;
            case 2:
                System.out.println("Do you want to check continually? (Y/n)");
                String s;
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
                if (s.charAt(0) == 'y')
                {
                    Timer t=new Timer();
                    t.scheduleAtFixedRate(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {checkSite(separator," ");}
                            catch(IOException e){}
                        } 
                    },0,30000);
                }
                else
                    checkSite(separator,"");
                break;
            case 3:
                clearScreen();
                String[] dirs = findDirs(separator);
                int length=dirs.length,i=0;
                if(length!=0)
                {
                    for(i=0;i<length;i++)
                        System.out.println((i+1)+") "+dirs[i]);
                    sc=new Scanner(System.in);
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
                    while(i<1 || i>length);
                    String path=findHome()+separator+"SiteAlert"+separator+dirs[i-1];
                    File f=new File(path);
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
                else
                    System.out.println("You haven't checked any site!");
                break;
        }
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
      while ((x = sc.nextInt()) > 4)
        if (x != 4)
          visualizeMenu();
      return x;
    }
    catch (IOException e)
    {
      clearScreen();
      System.out.println("Wrong input");
      return 4;
    }
  }
  public static void visualizeMenu() throws IOException
  {
      clearScreen();
      System.out.println("What do you want to do?\n1) Add new site to check\n2) Check sites\n3) Delete a site\n4) Exit");
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
        BufferedReader br = null;
        String[] dirs = f.list();
        return dirs;
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
      System.out.println("Insert an email where you want to be informed: ");
      email = sc.nextLine();
    }
    try
    {
      URL urli = new URL(sito);
      HttpURLConnection http = (HttpURLConnection)urli.openConnection();
      http.setUseCaches(false);
      int risposta = http.getResponseCode();
      if (risposta == 404)
      {
        System.out.println("This page doesn't exist!");
      }
      else if (risposta == 200)
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
  public static void checkSite(String separator,String s) throws IOException
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
                          System.out.println("Do you want to update your local file?(Y/n)");
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
                          {sendMail(mail,dir);}
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
          if(s.equals(""))
          {
            System.out.print("Press enter to continue...");
            sc.nextLine();
          }
      }
      else
          System.out.println("You haven't checked any site!");
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
  }
  public static String separator()
  {
      if(System.getProperty("os.name").contains("Windows"))
          return "\\";
      else
          return "/";
  }
  public static void sendMail(String to, String subject) throws AddressException
  {
      Properties p = new Properties();
      p.put("mail.smtp.host", "smtp.net.vodafone.it");
      p.put("mail.smtp.port", "25");
      Session s = Session.getDefaultInstance(p);
      Message m = new MimeMessage(s);
      try 
      {
          m.setFrom(new InternetAddress("SiteAlert@gmail.it")); //DON'T CHANGE IT
          m.setRecipient(RecipientType.TO, new InternetAddress(to));
          m.setSubject("The site \""+subject+"\" has been changed!");
          m.setText("The site \""+subject+"\" has been changed!");
          Transport.send(m);
      }
      catch (MessagingException e) {}
  }
}