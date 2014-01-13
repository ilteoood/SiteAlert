/*
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

public class SiteAlert
{
  public static void main(String[] args)
    throws IOException
  {
    Scanner sc = new Scanner(System.in);
    int x = choice();
    while (x != 4)
    {
      if (x == 1)
      {
        clearScreen();
        addSite(null, null);
      }
      else if (x == 2)
      {
        String[] dirs=findDirs();
        String path = findHome() + "/SiteAlert/";
        BufferedReader br;
        File f;
        URL urli = null;
        HttpURLConnection http = null;
        if(dirs.length!=0)
        {
            for (String dir : dirs)
            {
              String pathModificata = path + dir + "/sito.txt";
              try
              {
                f = new File(pathModificata);
                br = new BufferedReader(new FileReader(f));
                String sito = br.readLine();
                String contenutoLocale = br.readLine();
                urli = new URL(sito);
                http = (HttpURLConnection)urli.openConnection();
                br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                String contenutoRemoto = br.readLine();
                if (contenutoRemoto.equals(contenutoLocale)) 
                  System.out.println("The site \"" + dir + "\" hasn't been changed!");
                else 
                {
                  System.out.println("The site \"" + dir + "\" has been changed!\nDo you want to update the local file? (y/n)");
                  while (((contenutoLocale = sc.nextLine()).length() == 0) || ((contenutoLocale.charAt(0) != 'n') && (contenutoLocale.charAt(0) != 'y')))
                    System.out.println("Wrong input, do you want to update the local file? (y/n)");
                  char c = contenutoLocale.charAt(0);
                  if (c == 'y')
                    addSite(sito, dir);
                }
                System.out.print("Press enter to continue...");
                sc.nextLine();
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
            System.out.println("You haven't checked any site!");
      }
      else if(x==3)
      {
          clearScreen();
          String dirs[]=findDirs();
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
              String path=findHome()+"/SiteAlert/"+dirs[i-1];
              File f=new File(path);
              if(f.delete())
                  System.out.println("Site successfully deleted!");
              else
              {
                  String[] content=f.list();
                  for(String s:content)
                  {
                      f=new File(path+"/"+s);
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
      }
      x = choice();
    }
  }

  public static void clearScreen() throws IOException
  {
    String clearCommand;
    if (System.getProperty("os.name").contains("Windows"))
        clearCommand="cls";
    else
        clearCommand="clear";
    Process p=Runtime.getRuntime().exec(clearCommand);
    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    System.out.println(br.readLine());
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
    catch (Exception e)
    {
      clearScreen();
      System.out.println("Wrong input");
      return 4;
    }
  }

  public static void visualizeMenu() throws IOException
  {
      clearScreen();
      System.out.println("What do you want to do?");
      System.out.println("1) Add a new site to check");
      System.out.println("2) Check sites");
      System.out.println("3) Delete a site");
      System.out.println("4) Exit");
  }

  public static String findHome() throws IOException 
  {
    return System.getProperty("user.home");
  }
  public static String[] findDirs() throws IOException
  {
        String path = findHome() + "/SiteAlert/";
        File f = new File(path);
        if(!f.exists())
            f.mkdir();
        BufferedReader br = null;
        String[] dirs = f.list();
        return dirs;
  }
  public static void addSite(String site, String nameSite) throws IOException 
  {
    String sito = site; String nomeSito = nameSite;
    if ((sito == null) && (nomeSito == null))
    {
      Scanner sc = new Scanner(System.in);
      System.out.println("Insert the link for the site: ");
      sc = new Scanner(System.in);
      sito = sc.nextLine();
      System.out.println("Insert a name for the site: ");
      nomeSito = sc.nextLine();
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
        String path = findHome() + "/SiteAlert/" + nomeSito;
        File f = new File(path);
        if (!f.isDirectory())
          if (!f.mkdirs())
            System.out.println("I can't create the necessary directory!");
        else
        {
          path = path + "/sito.txt";
          f = new File(path);
          if (f.exists())
            f.delete();
          FileWriter fw = new FileWriter(f);
          fw.write(sito + "\n");
          BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
          while ((sito = br.readLine()) != null)
            fw.write(sito + "\n");
          fw.flush();
          fw.close();
        }
      }
    }
    catch (MalformedURLException e)
    {
      System.out.println("There is an error with the link!");
    }
  }
}