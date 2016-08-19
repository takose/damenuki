import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.*; 
import java.util.regex.Pattern; 
import java.util.regex.Matcher; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class damenuki extends PApplet {





boolean activate=false, start=true;
String filePath, usePath, notUsePath;
String[] fileArray;
HashMap<String, Pic> img = new HashMap<String, Pic>();
File directory;

int select;
int current = 0;

int baseColor=color(255*2/3), useColor=color(255/3), notUseColor=color(255*2/3), startColor=color(0, 0, 0);
int imgWidth=1320, imgHeight=880, menuHeight=150, buf = 350;
int rightColor=0, leftColor=0;

public void setup() {
  PFont font = createFont("MS Gothic", 48, true);
  textFont(font);
  textSize(20);
  textAlign(CENTER);
  size(imgWidth+buf, imgHeight);
  noStroke();
  noLoop();
  makeUI();
}
public void draw() {
  if (activate) {
    while (!img.containsKey (fileArray[current])) {
      current++;
    }
    image(img.get(fileArray[current]).pic, 0, 0, imgWidth, imgHeight);
  }

  fill(0, leftColor);
  rect(0, height-50, imgWidth/2, 50);
  fill(255, rightColor);
  rect((imgWidth)/2, imgHeight-50, imgWidth/2, 50);
  if (rightColor > 0)rightColor-=20;
  if (leftColor > 0)leftColor-=20;
}

public void keyPressed() {
  if (keyCode == DOWN) {
    if (current<fileArray.length-1)current++;
    loadImg();
  }
  if (keyCode == UP) {
    if (current>0)current--;
  }
  if (keyCode == RIGHT) {
    File tmp = new File(usePath+fileArray[current]);
    img.get(fileArray[current]).dir.renameTo(tmp);
    PImage tmpImg = img.get(fileArray[current]).pic;
    img.remove(fileArray[current]);
    Pic _img = new Pic(tmp, tmpImg);
    img.put(fileArray[current], _img);
    rightColor=255;
  }
  if (keyCode == LEFT) {
    File tmp = new File(notUsePath+fileArray[current]);
    img.get(fileArray[current]).dir.renameTo(tmp);
    PImage tmpImg = img.get(fileArray[current]).pic;
    img.remove(fileArray[current]);
    Pic _img = new Pic(tmp, tmpImg);
    img.put(fileArray[current], _img);
    leftColor=255;
  }
  makeUI();
}

public void mousePressed() {
  if (mouseX>imgWidth) {
    if (mouseY<menuHeight) {
      select=0;
      selectFolder("Select a file to process:", "fileSelected");
    }
    if (mouseY>menuHeight && mouseY<menuHeight*2) {
      select=1;
      selectFolder("Select a file to process:", "fileSelected");
    }
    if (mouseY>menuHeight*2 && mouseY<menuHeight*3) {
      select=2;
      selectFolder("Select a file to process:", "fileSelected");
    }
    if (mouseY>menuHeight*3 && mouseY<menuHeight*4) {
      if (filePath!=null && usePath!=null && notUsePath!=null && start) {
        start=false;
        directory = new File(filePath);
        fileArray = directory.list();
        if (fileArray != null) {
          loadImg();
          makeUI();
        } else {
          System.out.println(directory.toString() + "\u3000\u306f\u5b58\u5728\u3057\u307e\u305b\u3093" );
          exit();
        }
        activate=true;
        loop();
      }
    }
  }
}

public void loadImg() {
  int i=0, j=current;
  while (i < 3 && j<fileArray.length) {
    String regex = "/(jpeg)|(jpg)|(JPEG)|(JPG)|(png)|(PNG)$/";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(fileArray[j]);
    if (m.find()) {
      if (!img.containsKey(fileArray[j])) {
        PImage tmpimg = loadImage(filePath+fileArray[j]);
        File tmpfile = new File(filePath+fileArray[j]);
        img.put(fileArray[j], new Pic(tmpfile, tmpimg));
        System.out.println(fileArray[j]);
      }
      i++;
    }
    j++;
  }
}
public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    println("User selected " + selection.getAbsolutePath());
    if (select==0) {
      filePath = selection.getAbsolutePath() + "/";
    }
    if (select==1) {
      usePath = selection.getAbsolutePath() + "/";
    }
    if (select==2) {
      notUsePath = selection.getAbsolutePath() + "/";
    }
  }
  makeUI();
  redraw();
}
public void makeUI() {
  if (!activate) {
    background(255);
    fill(0);

    String guide="";
    if (filePath==null)guide+="\u30c0\u30e1\u629c\u304d\u5143\u30d5\u30a9\u30eb\u30c0 ";
    if (usePath==null)guide+="\u4f7f\u7528\u753b\u50cf\u7528\u30d5\u30a9\u30eb\u30c0 ";
    if (notUsePath==null)guide+="\u4e0d\u4f7f\u7528\u753b\u50cf\u7528\u30d5\u30a9\u30eb\u30c0 ";
    if (guide!="")text(guide + "\u3092\u6e96\u5099\u3057\u3001\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044", imgWidth/2, imgHeight/2);
  }
  fill(baseColor);
  rect(imgWidth, 0, buf, menuHeight);
  fill(useColor);
  rect(imgWidth, menuHeight*1, buf, menuHeight);
  fill(notUseColor);
  rect(imgWidth, menuHeight*2, buf, menuHeight);
  fill(startColor);
  rect(imgWidth, menuHeight*3, buf, menuHeight);
  fill(255);


  fill(255);
  if (filePath==null) {
    text("\u9078\u629e...", imgWidth+buf/2, menuHeight/2);
  } else {
    textSize(15);
    text(filePath, imgWidth, menuHeight/3, buf-10, menuHeight/2);
    textSize(20);
    text("\u4fee\u6b63...", imgWidth+buf/2, menuHeight*5/6);
    if (activate && img.get(fileArray[current]).dir.getAbsolutePath().equals(filePath+fileArray[current])) {
      fill(0xffc93a40);
    }
  }
  text("\u30c0\u30e1\u629c\u304d\u5143\u30d5\u30a9\u30eb\u30c0", imgWidth+buf/2, menuHeight/4);
  fill(255);

  if (usePath==null) {
    text("\u9078\u629e...", imgWidth+buf/2, menuHeight+menuHeight/2);
  } else {
    textSize(15);
    text(usePath, imgWidth, menuHeight+menuHeight/3, buf-10, menuHeight+menuHeight/2);
    textSize(20);
    text("\u4fee\u6b63...", imgWidth+buf/2, menuHeight+menuHeight*5/6);
    if (activate && img.get(fileArray[current]).dir.getAbsolutePath().equals(usePath+fileArray[current])) {
      fill(0xffd685b0);
    }
  }
  text("\u4f7f\u7528\u753b\u50cf\u7528\u30d5\u30a9\u30eb\u30c0", imgWidth+buf/2, menuHeight+menuHeight/4);
  fill(255);
  if (notUsePath==null) {
    text("\u9078\u629e...", imgWidth+buf/2, menuHeight*2+menuHeight/2);
  } else {
    textSize(15);
    text(notUsePath, imgWidth, menuHeight*2+menuHeight/3, buf-10, menuHeight*2+menuHeight/2);
    textSize(20);
    text("\u4fee\u6b63...", imgWidth+buf/2, menuHeight*2+menuHeight*5/6);
    if (activate && img.get(fileArray[current]).dir.getAbsolutePath().equals(notUsePath+fileArray[current])) {
      fill(0xffc93a40);
    }
  }
  text("\u4e0d\u4f7f\u7528\u753b\u50cf\u7528\u30d5\u30a9\u30eb\u30c0", imgWidth+buf/2, menuHeight*2+menuHeight/4);
  fill(255);
  if (filePath!=null && usePath!=null && notUsePath!=null) {
    if (start) {
      fill(255);
    } else {
      fill(128);
    }
    PImage ope = loadImage("operation.png");
    image(ope, imgWidth, menuHeight*4+30, buf, ope.height*buf/ope.width);
  } else {
    fill(128);
  }
  text("\u30c0\u30e1\u629c\u304d\u958b\u59cb!!", imgWidth+buf/2, menuHeight*3+menuHeight/2);
}

class Pic{
  File dir;
  PImage pic;
  Pic(File _dir, PImage _pic){
    dir = _dir;
    pic = _pic;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "damenuki" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
