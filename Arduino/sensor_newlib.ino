#include <SoftwareSerial.h>
#include <GT5X.h>
#include <Adafruit_VC0706.h>

SoftwareSerial fserial(4, 2);
SoftwareSerial cameraconnection(6, 7);
Adafruit_VC0706 cam = Adafruit_VC0706(&cameraconnection);
GT5X finger(&fserial);
GT5X_DeviceInfo ginfo;

void setup()
{
  Serial.begin(9600);
  // Serial.println("ENROLL test");

  pinMode(LED_BUILTIN, OUTPUT);
}

uint8_t temp_buf[GT5X_TEMPLATESZ];

void loop()
{
  // while (Serial.read() != -1);  // clear buffer

  if (Serial.available() > 0) {

    int inByte = Serial.read();
    if (inByte == 97) {

      fserial.begin(9600);
      if (finger.begin(&ginfo)) {
      } else {
        while (1) yield();
      }
      finger.delete_id(0);
      uint16_t fid = 0;

      char c = '0';
      fid *= 10;
      fid += c - '0';


      if (is_enrolled(fid))
        return;

      enroll_finger(fid);
      finger.set_led(false);
      get_template_buff(fid, temp_buf, GT5X_TEMPLATESZ);
      finger.end();
    } else if (inByte == 98) {
      digitalWrite(LED_BUILTIN, HIGH);
      cam_get();
      digitalWrite(LED_BUILTIN, LOW);
    } else if (inByte == 99) {
      
      fserial.begin(9600);
      if (finger.begin(&ginfo)) {
      } else {
        while (1) yield();
      }
      
      // Replace with cloud data

      int i = 0;
      int x = 0;
      int k = 0;
      char buffer[3];
      char j;

      while ( k < 498) {
        delay(1);
        if (Serial.available()) {
          j = Serial.read();

          if (j == ' ' or j == '\0') {

            if (x == 1) {
              temp_buf[k++] = buffer[0];
            } else if (x == 2) {
              temp_buf[k++] = buffer[0] * 10 + buffer[1];
            } else {
              temp_buf[k++] = buffer[0] * 100 + buffer[1] * 10 + buffer[2];
            }
            x = 0;
          } else {
            buffer[x++] = j - 48;
          }
        }
      }

      empty_database();
      set_template(1, temp_buf, GT5X_TEMPLATESZ);
      finger.set_led(true);
      verify_finger(1);
      finger.set_led(false);
      empty_database();
      finger.end();
    } else {

      Serial.print("");
    }
  }
}

int StrToHex(char str[])
{
  return strtol(str, 0, 16);
}

bool is_enrolled(uint16_t fid) {
  uint16_t rc = finger.is_enrolled(fid);
  switch (rc) {
    case GT5X_OK:
      //Serial.println("ID is used!");
      return true;
    case GT5X_NACK_IS_NOT_USED:
      //Serial.print("ID "); Serial.print(fid);
      //Serial.println(" is free.");
      return false;
    default:
      Serial.print("Error");
      return true;
  }
}

void enroll_finger(uint16_t fid) {
  uint16_t p = finger.start_enroll(fid);
  switch (p) {
    case GT5X_OK:
      // Serial.print("Enrolling ID #"); Serial.println(fid);
      break;
    default:
      Serial.print("Error");
      return;
  }

  /* turn on led for print capture */
  finger.set_led(true);

  /* scan finger 3 times */
  for (int scan = 1; scan < 4; scan++) {
    Serial.println("Finger");
    p = finger.capture_finger(true);

    while (p != GT5X_OK) {
      p = finger.capture_finger(true);
      switch (p) {
        case GT5X_OK:
          // Serial.println("Image taken.");
          break;
        case GT5X_NACK_FINGER_IS_NOT_PRESSED:
          // Serial.println(".");
          break;
        default:
          Serial.print("Error");
          return;
      }
      yield();
    }

    p = finger.enroll_scan(scan);
    switch (p) {
      case GT5X_OK:
        // Serial.print("Scan "); Serial.print(scan);
        //Serial.println("Scan Complete");
        break;
      case GT5X_NACK_ENROLL_FAILED:
        Serial.println("Failed");
        return;
      case GT5X_NACK_BAD_FINGER:
        Serial.println("Fingerprint unclear");
        return;
      default:
        //Serial.print("Print already exists at ID ");
        //Serial.println(p);
        Serial.print("Exists");
        return;
    }

    Serial.println("Remove");
    while (finger.is_pressed()) {
      yield();
    }
    //Serial.println();
  }

  /* wr're done so turn it off */
  finger.set_led(false);

  Serial.println("Enroll Complete");
}

uint8_t template_buf[GT5X_TEMPLATESZ];

void get_template(uint16_t fid) {
  uint16_t rc = finger.get_template(fid);
  switch (rc) {
    case GT5X_OK:
      break;
    case GT5X_NACK_INVALID_POS:
    case GT5X_NACK_IS_NOT_USED:
    case GT5X_NACK_DB_IS_EMPTY:
      Serial.println("Error");
      return;
    default:
      Serial.print("Error");
      return;
  }

  // Serial.print("Getting template for ID "); Serial.println(fid);
  // Serial.println("\r\n------------------------------------------------");
  bool ret = finger.read_raw(GT5X_OUTPUT_TO_BUFFER, template_buf, GT5X_TEMPLATESZ);

  if (!ret) {
    Serial.println("Error");
    return;
  }

  /* just for pretty-printing */
  uint8_t num_rows = GT5X_TEMPLATESZ / 16;
  uint8_t num_cols = 16;

  for (int row = 0; row < num_rows; row++) {
    for (int col = 0; col < num_cols; col++) {
      Serial.print(template_buf[row * num_cols + col] < 16 ? "0" : "");
      Serial.print(template_buf[row * num_cols + col], HEX);
      Serial.print(" ");
    }
    // Serial.println();
    yield();
  }

  for (uint8_t remn = 0; remn < GT5X_TEMPLATESZ % (num_rows * num_cols); remn++) {
    Serial.print(template_buf[num_rows * num_cols + remn], HEX);
    Serial.print(" ");
  }

  //Serial.println("\r\n------------------------------------------------");
  // Serial.print(GT5X_TEMPLATESZ); Serial.println(" bytes read.");
}


bool get_template_buff(uint16_t fid, uint8_t * buffer, uint16_t to_read) {
  uint16_t rc = finger.get_template(fid);
  switch (rc) {
    case GT5X_OK:
      break;
    case GT5X_NACK_INVALID_POS:
    case GT5X_NACK_IS_NOT_USED:
    case GT5X_NACK_DB_IS_EMPTY:
      Serial.println("ID is unused!");
      return false;
    default:
      Serial.print("Error code: 0x"); Serial.println(rc, HEX);
      return false;
  }

  // Serial.print("Getting template for ID "); Serial.println(fid);
  // Serial.println("\r\n------------------------------------------------");
  bool ret = finger.read_raw(GT5X_OUTPUT_TO_BUFFER, buffer, to_read);

  if (!ret) {
    Serial.println("Template read failed!");
    return false;
  }

  /* just for pretty-printing */
  uint8_t num_rows = to_read / 16;
  uint8_t num_cols = 16;

  for (int row = 0; row < num_rows; row++) {
    for (int col = 0; col < num_cols; col++) {
      // Serial.print(buffer[row * num_cols + col] < 16 ? "0" : "");
      Serial.print(buffer[row * num_cols + col]);
      Serial.print(" ");
    }
    // Serial.println();
    yield();
  }

  for (uint8_t remn = 0; remn < to_read % (num_rows * num_cols); remn++) {
    Serial.print(buffer[num_rows * num_cols + remn]);
    Serial.print(" ");
  }

  // Serial.println("\r\n------------------------------------------------");
  // Serial.print(GT5X_TEMPLATESZ); Serial.println(" bytes read.");

  return true;
}

bool set_template(uint16_t fid, uint8_t * buffer, uint16_t to_write) {
  /* check for any duplicates, by default */
  uint16_t rc = finger.set_template(fid, 0);
  switch (rc) {
    case GT5X_OK:
      break;
    case GT5X_NACK_INVALID_POS:
      Serial.println("ID is invalid!");
      return false;
    default:
      Serial.print("Error code: 0x"); Serial.println(rc, HEX);
      return false;
  }

  // Serial.print("Transferring template to ID "); Serial.println(fid);

  /* now upload the template to the sensor, expect a response */
  rc = finger.write_raw(buffer, to_write, true);

  switch (rc) {
    case GT5X_OK:
      Serial.print("Transfer");
      return true;
    case GT5X_NACK_COMM_ERR:
    case GT5X_NACK_DEV_ERR:
      Serial.print("Error");
      return false;
    default:
      Serial.print("Error");
      return false;
  }
}

void verify_finger(uint16_t fid) {
  // Serial.println("Press Finger on Sensor");
  while (!finger.is_pressed())
    yield();

  uint16_t rc = finger.capture_finger();
  if (rc != GT5X_OK)
    return;

  rc = finger.verify_finger_with_template(fid);
  if (rc != GT5X_OK) {
    Serial.print("No Match");
    return;
  }

  Serial.print("Match");
}

void empty_database(void) {
  uint16_t rc = finger.empty_database();
//  switch (rc) {
//    case GT5X_OK:
//      Serial.println("Database empty.");
//      break;
//    case GT5X_NACK_DB_IS_EMPTY:
//      Serial.println("Database is already empty!");
//      break;
//    default:
//      Serial.print("Error code: 0x"); Serial.println(rc, HEX);
//      break;
//  }

  // Serial.println();
}

// IRIS
void cam_get() {
  // Try to locate the camera
  if (cam.begin()) {
    Serial.println("Camera Found");
  } else {
    Serial.println("No Camera");
    return;
  }

  char *reply = cam.getVersion();
  if (reply == 0) {
    Serial.print("Error");
  }
  cam.setImageSize(VC0706_640x480);        // biggest
  //cam.setImageSize(VC0706_320x240);        // medium
  //cam.setImageSize(VC0706_160x120);          // small
  uint8_t imgsize = cam.getImageSize();

  Serial.println("Snap in 3");
  delay(3000);

  if (! cam.takePicture())
    Serial.println("Failed");
  uint16_t jpglen = cam.frameLength();

  int32_t time = millis();
  pinMode(8, OUTPUT);
  // Read all the data up to # bytes!
  byte wCount = 0; // For counting # of writes
  while (jpglen > 0) {
    // read 32 bytes at a time;
    uint8_t *buffer;
    uint8_t bytesToRead = min((uint16_t)32, jpglen); // change 32 to 64 for a speedup but may not work with all setups!
    buffer = cam.readPicture(bytesToRead);

    char foo [20];
    sprintf(foo, "%02x", (*buffer));
    Serial.print(foo);
    Serial.print(" ");
    jpglen -= bytesToRead;
  }
  Serial.println("Done");
}

void delete_finger(uint16_t fid) {    
    uint16_t rc = finger.delete_id(fid);
    switch (rc) {
        case GT5X_OK:
            Serial.print("ID "); Serial.print(fid); 
            Serial.println(" deleted.");
            break;
        case GT5X_NACK_INVALID_POS:
            Serial.println("ID not used!");
            break;
        case GT5X_NACK_DB_IS_EMPTY:
            Serial.println("Database is empty!");
            break;
        default:
            Serial.print("Error code: 0x"); Serial.println(rc, HEX);
            break;
    }
}
