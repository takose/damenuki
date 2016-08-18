import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

boolean activate=false;
String filePath, usePath, notUsePath;
String[] fileArray;
HashMap<String, Pic> img = new HashMap<String, Pic>();
File directory;

int select;
int current = 0;

color baseColor=color(255*2/3),useColor=color(255/3), notUseColor=color(255*2/3), startColor=color(0, 0, 0);
int imgWidth=1320, imgHeight=880, menuHeight=150, buf = 200;
int rightColor=0, leftColor=0;

void setup() {
  PFont font = createFont("MS Gothic", 48, true);
  textFont(font);
  textSize(20);
  textAlign(CENTER);
  size(imgWidth+buf, imgHeight);
  noStroke();
  noLoop();
  makeUI();
}
void draw() {
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

void keyPressed() {
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
}

void mousePressed() {
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
      if (filePath!=null && usePath!=null && notUsePath!=null) {
        directory = new File(filePath);
        fileArray = directory.list();
        if (fileArray != null) {
          loadImg();
        } else {
          System.out.println(directory.toString() + "　は存在しません" );
          exit();
        }
        activate=true;
        loop();
      }
    }
  }
}

void loadImg() {
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
void fileSelected(File selection) {
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
void makeUI() {
  background(255);
  fill(0);
  
  String guide="";
  if(filePath==null)guide+="ダメ抜き元フォルダ ";
  if(usePath==null)guide+="使用画像用フォルダ ";
  if(notUsePath==null)guide+="不使用画像用フォルダ ";
  if(guide!="")text(guide + "を選択してください",imgWidth/2,imgHeight/2);
  
  fill(baseColor);
  rect(imgWidth, 0, buf, menuHeight);
  fill(useColor);
  rect(imgWidth, menuHeight*1, buf, menuHeight);
  fill(notUseColor);
  rect(imgWidth, menuHeight*2, buf, menuHeight);
  fill(startColor);
  rect(imgWidth, menuHeight*3, buf, menuHeight);
  fill(255);
  
  text("ダメ抜き元フォルダ", imgWidth+buf/2, menuHeight/4);
  if (filePath==null) {
    text("選択...", imgWidth+buf/2, menuHeight/2);
  } else {
    textSize(15);
    text(filePath, imgWidth, menuHeight/3, buf-10, menuHeight/2);
    textSize(20);
    text("修正...", imgWidth+buf/2, menuHeight*5/6);
  }
  text("使用画像用フォルダ", imgWidth+buf/2, menuHeight+menuHeight/4);
  if (usePath==null) {
    text("選択...", imgWidth+buf/2, menuHeight+menuHeight/2);
  } else {
    textSize(15);
    text(usePath, imgWidth, menuHeight+menuHeight/3, buf-10, menuHeight+menuHeight/2);
    textSize(20);
    text("修正...", imgWidth+buf/2, menuHeight+menuHeight*5/6);
  }
  text("不使用画像用フォルダ", imgWidth+buf/2, menuHeight*2+menuHeight/4);
  if (notUsePath==null) {
    text("選択...", imgWidth+buf/2, menuHeight*2+menuHeight/2);
  } else {
    textSize(15);
    text(notUsePath, imgWidth, menuHeight*2+menuHeight/3, buf-10, menuHeight*2+menuHeight/2);
    textSize(20);
    text("修正...", imgWidth+buf/2, menuHeight*2+menuHeight*5/6);
  }
  if (filePath!=null && usePath!=null && notUsePath!=null) {
    fill(255);
  } else {
    fill(128);
  }
  text("ダメ抜き開始!!", imgWidth+buf/2, menuHeight*3+menuHeight/2);
}

