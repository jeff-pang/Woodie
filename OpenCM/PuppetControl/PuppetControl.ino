/* Minimum_Source*/
#define DXL_BUS_SERIAL 3   //Dynamixel on Serial3(USART3)  <-OpenCM 485EXP
#define MOVING 610

Dynamixel Dxl(DXL_BUS_SERIAL);

void setup() {
  
  pinMode(BOARD_LED_PIN, OUTPUT);
  Dxl.begin(3);
  
  // put your setup code here, to run once:
  Dxl.jointMode(3);
  Dxl.torqueDisable(3);
  Dxl.jointMode(6);  
  Dxl.torqueDisable(6);
  Dxl.jointMode(9);
  Dxl.torqueDisable(9);
  Dxl.jointMode(13);
  Dxl.torqueDisable(13);
  Dxl.jointMode(15);
  Dxl.torqueDisable(15);
}

void loop() {
  // put your main code here, to run repeatedly:
  byte result[5][4];
  
  getDxlInfo(3,result[0]);
  getDxlInfo(6,result[1]);
  getDxlInfo(9,result[2]);
  getDxlInfo(13,result[3]);
  getDxlInfo(15,result[4]);
  
  byte allResults[20];
  
  for(int x=0;x<5;x++)
  {
    for(int y=0;y<4;y++)
    {
      allResults[(x*4)+y]=result[x][y];
    }
  }
  
  SerialUSB.write(allResults,20);
  delay(100);
}

void getDxlInfo(byte dxlid,byte* output)
{  
  byte moving=Dxl.isMoving(dxlid);
  int pos=Dxl.getPosition(dxlid);
  
  byte bpos1= pos;
  byte bpos2= pos >> 8;

  output[0]=dxlid;
  output[1]=moving;
  output[2]=bpos1;
  output[3]=bpos2;
}
