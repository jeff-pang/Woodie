/* Minimum_Source*/
#define DXL_BUS_SERIAL 3   //Dynamixel on Serial3(USART3)  <-OpenCM 485EXP
#define MOVING 610

Dynamixel Dxl(DXL_BUS_SERIAL);

byte pollBuffer[3];
boolean pollStart;

void setup() {
  
  pollBuffer={1,1,2};  
  
  pollStart=false;
  // put your setup code here, to run once:
  SerialUSB.attachInterrupt(usbInterrupt);
  pinMode(BOARD_LED_PIN, OUTPUT);
  Dxl.begin(3);

  Dxl.jointMode(1);
  Dxl.jointMode(2);
  Dxl.jointMode(4);
  Dxl.jointMode(11);
  Dxl.jointMode(17);
  
}

void loop() {
  
  byte result[5];
  getDxlInfo(pollBuffer,result);    
  SerialUSB.write(result,5);
  
  delay(200);
}

void usbInterrupt(byte* buffer, byte nCount){

  int cmd=buffer[0];

  if(cmd==0)
  {
    moveAction(buffer);
  }
  
  
  if(cmd ==1)
  {
    for(int x=0;x<3;x++)
    {
      pollBuffer[x] = buffer[x];
    }
    
    pollStart=true;
  }
  
  if(cmd == 2)
  {
    pollStart=false;
  }
}

void getDxlInfo(byte* input,byte* output)
{  
  byte reqid=input[1];
  byte dxlid=input[2];    
  byte moving=Dxl.isMoving(dxlid);
  int pos=Dxl.getPosition(dxlid);
  
  byte bpos1= pos;
  byte bpos2= pos >> 8;

  output[0]=reqid;
  output[1]=dxlid;
  output[2]=moving;
  output[3]=bpos1;
  output[4]=bpos2;
}

void moveAction(byte* buffer)
{
  byte reqid=buffer[1];
  byte dxlid=buffer[2];

  int goalpos= buffer[3] | buffer[4] << 8;
  int speed = buffer[5] | buffer[6] << 8;

  Dxl.setPosition(dxlid,goalpos,speed);    
}

