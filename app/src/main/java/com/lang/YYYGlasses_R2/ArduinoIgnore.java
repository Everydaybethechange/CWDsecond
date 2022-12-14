package com.lang.YYYGlasses_R2;

public class ArduinoIgnore {


/**************************************************************************
 This is an example for our Monochrome OLEDs based on SSD1306 drivers

 Pick one up today in the adafruit shop!
 ------> http://www.adafruit.com/category/63_98

 This example is for a 128x32 pixel display using I2C to communicate
 3 pins are required to interface (two I2C and one reset).

 Adafruit invests time and resources providing this open
 source code, please support Adafruit and open-source
 hardware by purchasing products from Adafruit!

 Written by Limor Fried/Ladyada for Adafruit Industries,
 with contributions from the open source community.
 BSD license, check license.txt for more information
 All text above, and the splash screen below must be
 included in any redistribution. .
 **************************************************************************/

#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define SCREEN_WIDTH 128 // OLED display width, in pixels
            #define SCREEN_HEIGHT 32 // OLED display height, in pixels

// Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
// The pins for I2C are defined by the Wire-library.
// On an arduino UNO:       A4(SDA), A5(SCL)
// On an arduino MEGA 2560: 20(SDA), 21(SCL)
// On an arduino LEONARDO:   2(SDA),  3(SCL), ...
            #define OLED_RESET     4 // Reset pin # (or -1 if sharing Arduino reset pin)
            #define SCREEN_ADDRESS 0x3C ///< See datasheet for Address; 0x3D for 128x64, 0x3C for 128x32
    Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);


#define LOGO_HEIGHT   16
            #define LOGO_WIDTH    16

    void setup() {
        Serial.begin(9600);

        // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
        if(!display.begin(SSD1306_SWITCHCAPVCC, SCREEN_ADDRESS)) {
            Serial.println(F("SSD1306 allocation failed"));
            for(;;); // Don't proceed, loop forever
        }

        // Show initial display buffer contents on the screen --
        // the library initializes this with an Adafruit splash screen.
        display.display();
        delay(2000); // Pause for 2 seconds

        // Clear the buffer
        display.clearDisplay();

        // Draw a single pixel in white
        display.drawPixel(10, 10, SSD1306_WHITE);

        // Show the display buffer on the screen. You MUST call display() after
        // drawing commands to make them visible on screen!
        display.display();
        delay(2000);
        // display.display() is NOT necessary after every single drawing command,
        // unless that's what you want...rather, you can batch up a bunch of
        // drawing operations and then update the screen all at once by calling
        // display.display(). These examples demonstrate both approaches...



        testdrawchar();      // Draw characters of the default font




    }

    void loop() {
    }



    void testdrawchar(void) {
        display.clearDisplay();

        display.setTextSize(1);      // Normal 1:1 pixel scale
        display.setTextColor(SSD1306_WHITE); // Draw white text
        display.setCursor(0, 0);     // Start at top-left corner
        display.cp437(true);         // Use full 256 char 'Code Page 437' font

        // Not all the characters will fit on the display. This is normal.
        // Library will draw what it can and the rest will be clipped.
//  for(int16_t i=0; i<256; i++) {
//    if(i == '\n') display.write(' ');
//    else          display.write(i);
//  }
        display.println(F("okok"));
        display.display();
        delay(2000);
    }




#define XPOS   0 // Indexes into the 'icons' array in function below
            #define YPOS   1
            #define DELTAY 2








}
