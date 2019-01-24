package com.example.swat.axistechnical;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import fr.ganfra.materialspinner.MaterialSpinner;

public class UserAreaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    ImageView ivDeviceSerialNumber, ivGPS, ivCustomDate;
    Button btCameraDeviceSerial, btDeviceSerialGallery, btBarCode;
    FloatingActionButton FABsend;

    @SuppressLint("StaticFieldLeak")
    public static  EditText etDeviceSerialNumber;

    TextView tvSelectedDate, tvSelectedTime;
    MaterialSpinner spDeviceModel, spTaskType, spCustomer;
    CheckBox cbDateTime;
    TextInputLayout tfDeviceSerialNumber, tfVehiclePlateNumber, tfOdometer, tfVehicleMake, tfVehicleModel, tfFleet;
    EditText etVehiclePlateNumber, etVehicleMake, etVehicleModel, etOdometer, etFleet;
    ProgressDialog progressDialog;

    LocationManager locationManager;
    LocationListener locationListener;


    double latitude = 0.0;
    double longitude = 0.0;
    int day, month, year, hour, minute, second, id;
    long Day = 0;
    long OneYears;
    String appDate, appTime, appSelectDate, appSelectTime, name, TaskTypePostion, DeviceModelPostion, CustomerPostion;

    Uri pictureUri = null;

    ArrayList<String> arrayTask, arrayDeviceType, arrayCustomer;

    Toolbar toolbar;


    //----------------Device Serial Number----------------------------------------------------------
    int CAMERA_DEVICE_SERIAL_NUMBER = 228; //for camera button
    int GALLERY_DEVICE_SERIAL_NUMBER = 999; // for gallery button
    private static final int CROP_CAMERA_SERIAL_NUMBER = 100; //after crop image from camera
    private static final int CROP_GALLERY_SERIAL_NUMBER = 200;  // after crop image from gallery
    //----------------------------------------------------------------------------------------------



    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_area_activity);



        //-----------------Toolbar act as the action bar--------------------------------------------
        toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        //----------------Device Serial Number------------------------------------------------------
        btCameraDeviceSerial = findViewById(R.id.btCameraDeviceSerial);
        btDeviceSerialGallery = findViewById(R.id.btGallerySelect);
        ivDeviceSerialNumber = findViewById(R.id.IvDevice_serial_number);
        etDeviceSerialNumber = findViewById(R.id.etDeviceSerialNumber);
        tfDeviceSerialNumber = findViewById(R.id.tfDeviceSerialNumber);
        btBarCode = findViewById(R.id.btBarCode);


        //---------------Vehicle Plate Number-------------------------------------------------------
        etVehiclePlateNumber = findViewById(R.id.etVehiclePlateNumber);
        tfVehiclePlateNumber = findViewById(R.id.tfVehiclePlateNumber);

        //---------------Spinner Device Model-------------------------------------------------------
        spDeviceModel = findViewById(R.id.spDeviceModel);
        arrayDeviceType = new ArrayList<>();
        getDeviceSpinner(); // To get all elements to spinner from database
        spDeviceModel.setOnItemSelectedListener(this);


        //--------------Vehicle Make and Model------------------------------------------------------
        etVehicleMake = findViewById(R.id.etVehicleMake);
        etVehicleModel = findViewById(R.id.etVehicleModel);
        tfVehicleMake = findViewById(R.id.tfVehicleMake);
        tfVehicleModel = findViewById(R.id.tfVehicleModel);




        //--------Date and time---------------------------------------------------------------------
        ivCustomDate = findViewById(R.id.ivCustomDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        cbDateTime = findViewById(R.id.cbDateTime);



        //--------Odometer number-------------------------------------------------------------------
        etOdometer = findViewById(R.id.etOdometer);
        tfOdometer = findViewById(R.id.tfOdometer);



        //---------Task type spinner----------------------------------------------------------------
        spTaskType = findViewById(R.id.spTaskType);
        arrayTask = new ArrayList<>();
        getTaskSpinner(); // To get all elements to spinner from database
        spTaskType.setOnItemSelectedListener(this);


        //--------Fleet ID--------------------------------------------------------------------------
        etFleet = findViewById(R.id.etFleet);
        tfFleet = findViewById(R.id.tfFleet);


        //--------Company Name----------------------------------------------------------------------
        spCustomer = findViewById(R.id.spCustomer);
        arrayCustomer = new ArrayList<>();
        getCustomerSpinner(); // To get all elements to spinner from database
        spCustomer.setOnItemSelectedListener(this);



        //--------GPS Location----------------------------------------------------------------------
        ivGPS = findViewById(R.id.ivGPS);



        //------To get technical id & name from login page while login------------------------------
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("tec ID", -1);
        name = getIntent.getStringExtra("name");
        ActionBar actionBar = getSupportActionBar(); // To getActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(name);
        }

        //----------Send Data Floating Action Button -----------------------------------------------
        FABsend = findViewById(R.id.FABsend);



        //---------This thread to get time and date from system every sound-------------------------
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                long dateAndTime = System.currentTimeMillis(); // Get date and time in millis sound

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Set date format how it well display in text view

                                SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss"); // Set time format how it well display in text view

                                String dateString = dateFormat.format(dateAndTime); // Set date format in string
                                String timeString = timeFormat.format(dateAndTime); // Set time format in string

                                appDate = dateString;
                                appTime = timeString;

                                //tvCurrentDate.setText(dateString); // Set date string in text view
                                //tvCurrentTime.setText(timeString); // Set time string in text view


                                //----------------------To refresh GPS every one sound--------------
                                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                                locationListener = new LocationListener() {
                                    @SuppressLint({"SetTextI18n"})
                                    @Override // To get latitude and longitude from GPS
                                    public void onLocationChanged(Location location) {
                                        if(location != null)
                                        {
                                            ivGPS.setImageResource(R.drawable.ic_gps_on);
                                            latitude = location.getLatitude();
                                            longitude = location.getLongitude();
                                        }
                                        else
                                        {
                                            ivGPS.setImageResource(R.drawable.ic_gps_off);
                                        }
                                    }

                                    @Override
                                    public void onStatusChanged(String provider, int status, Bundle extras) {

                                    }

                                    @Override
                                    public void onProviderEnabled(String provider) {

                                    }

                                    @Override // To open GPS setting when GPS in close in phone
                                    public void onProviderDisabled(String provider) {
                                        ivGPS.setImageResource(R.drawable.ic_gps_off);
                                    }
                                };

                                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                                {
                                    // To update GPS location ever 1000 millisecond when it move 0 distance
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                                }
                                else
                                {
                                    // To update GPS location ever 1000 millisecond when it move 0 distance
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Toast.makeText(UserAreaActivity.this, "Error get time", Toast.LENGTH_LONG).show(); // If something waring happened
                }
            }
        };
        t.start();

        Day = 24*60*60*1000; // To count one day
        OneYears = (Day * 365); // To count one yeas => 365 is number of days in one year




        //-------------------Custom Date Button-----------------------------------------------------
        ivCustomDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar date = Calendar.getInstance();

                year = date.get(Calendar.YEAR);
                month = date.get(Calendar.MONTH);
                day = date.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UserAreaActivity.this, UserAreaActivity.this, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(date.getTimeInMillis() - OneYears);
                datePickerDialog.getDatePicker().setMaxDate(date.getTimeInMillis());
                datePickerDialog.show();


            }
        });


        //----------To hide and show current date or custom date------------------------------------
        cbDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cbDateTime.isChecked())
                {
                    Toast.makeText(UserAreaActivity.this, "Current Date & Time", Toast.LENGTH_SHORT).show();

                    // To hide custom date button with selected date and time text view
                    ivCustomDate.setVisibility(View.GONE);
                    tvSelectedDate.setVisibility(View.GONE);
                    tvSelectedTime.setVisibility(View.GONE);


                }
                else
                {
                    Toast.makeText(UserAreaActivity.this, "Custom Date & Time", Toast.LENGTH_SHORT).show();

                    // To show custom date button with selected date and time text view
                    ivCustomDate.setVisibility(View.VISIBLE);
                    tvSelectedDate.setVisibility(View.VISIBLE);
                    tvSelectedTime.setVisibility(View.VISIBLE);

                }
            }
        });




        //-------------Camera button in device serial number----------------------------------------
        btCameraDeviceSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeCameraSerialNumber();
            }
        });

        //---------------Gallery button in device serial number-------------------------------------
        btDeviceSerialGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImagesDeviceSerialNubmer();
            }
        });

        //-------------Barcode button in device serial number---------------------------------------
        btBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAreaActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        FABsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(UserAreaActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                hideKeyboard(UserAreaActivity.this);

                if(checkEmptyEditText(etDeviceSerialNumber, tfDeviceSerialNumber, "Empty Serial number !") &&
                        checkLengthDeviceSerialEditText(etDeviceSerialNumber, tfDeviceSerialNumber, "10 Digit only !") &&

                        checkEmptyEditText(etVehiclePlateNumber, tfVehiclePlateNumber, "Empty Vehicle plate !") &&
                        checkLengthPlateNumber(etVehiclePlateNumber, tfVehiclePlateNumber, "No more then 10 digit !") &&


                        checkEmptyEditText(etOdometer, tfOdometer, "Empty Odometer number !") &&
                        checkOdometerNumber(etOdometer,tfOdometer, "No more then 8 digit !") &&

                        checkEmptyEditText(etVehicleMake, tfVehicleMake, "Empty Vehicle make !") &&
                        checkEmptyEditText(etVehicleModel, tfVehicleModel, "Empty Vehicle model !") &&
                        checkVehicleModel(etVehicleModel, tfVehicleModel, "4 digit only !") &&

                        checkSpinner(TaskTypePostion, spTaskType,"Select Task Type !") &&
                        checkSpinner(DeviceModelPostion, spDeviceModel,"Select Device Model !") &&
                        checkSpinner(CustomerPostion, spCustomer, "Select Customer Name !") &&

                        checkGPS(latitude, longitude, "Refresh to get GPS location"))
                {




                    String deviceSerialNumber = etDeviceSerialNumber.getText().toString();
                    String vehiclePlateNumber = etVehiclePlateNumber.getText().toString();
                    String odoMeter = etOdometer.getText().toString();
                    String vehicleMake = etVehicleMake.getText().toString();
                    int vehicleModel = Integer.parseInt(etVehicleModel.getText().toString());
                    String timeInSend, dataInSend, fleetId;

                    if(etFleet.getText().toString().isEmpty()) // To check fleet id
                    {
                        fleetId = "null";
                    }
                    else
                    {
                        fleetId = etFleet.getText().toString();
                    }

                    if(cbDateTime.isChecked()) // To check current date or select date
                    {
                        Toast.makeText(UserAreaActivity.this, appDate +  "--------" + appTime, Toast.LENGTH_LONG).show();
                        timeInSend = appTime;
                        dataInSend = appDate;
                    }
                    else
                    {
                        Toast.makeText(UserAreaActivity.this, appSelectDate +  "--------" + appSelectTime, Toast.LENGTH_LONG).show();
                        timeInSend = appSelectTime;
                        dataInSend = appSelectDate;
                    }


                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success)
                                {
                                    etDeviceSerialNumber.setText(""); // Empty
                                    spDeviceModel.setSelection(0); // set default selection
                                    etVehiclePlateNumber.setText(""); // Empty
                                    etOdometer.setText(""); // Empty
                                    etVehicleMake.setText(""); // Empty
                                    etVehicleModel.setText(""); // Empty
                                    etFleet.setText(""); // Empty

                                    Toast.makeText(UserAreaActivity.this, "OK !", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(UserAreaActivity.this, "Cannot Send ", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    UserAreaRequest userAreaRequest = new UserAreaRequest(id, deviceSerialNumber,vehiclePlateNumber,CustomerPostion, odoMeter, fleetId, TaskTypePostion, DeviceModelPostion,vehicleMake,vehicleModel, latitude, longitude, dataInSend, timeInSend, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                    queue.add(userAreaRequest);
                    Toast.makeText(UserAreaActivity.this, "Send", Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog.dismiss();
                }


            }
        });


    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if(parent.getId()  == R.id.spDeviceModel)
        {
            DeviceModelPostion = parent.getItemAtPosition(position).toString();
        }

        if(parent.getId() == R.id.spTaskType)
        {
            TaskTypePostion = parent.getItemAtPosition(position).toString();
        }

        if(parent.getId() == R.id.spCustomer)
        {
            CustomerPostion = parent.getItemAtPosition(position).toString();
        }

    }  // To get selection item from spinner

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    } // do nothing no select

    private void invokeCameraSerialNumber() {

        // get a file reference
        pictureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), createImageFile()); // Make Uri file example file://storage/emulated/0/Pictures/Civil_ID20180924_180619.jpg

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Go to camera

        // tell the camera where to save the image.
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        // tell the camera to request WRITE permission.
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, CAMERA_DEVICE_SERIAL_NUMBER);

    } // open camera for device serial number

    private void selectImagesDeviceSerialNubmer() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_DEVICE_SERIAL_NUMBER);
    } // To select image from gallery in device serial number

    public File createImageFile() {
        // the public picture director
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // To get pictures directory from android system

        // timestamp makes unique name.
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        // put together the directory and the timestamp to make a unique image location.
        File imageFile = new File(picturesDirectory, timestamp + ".jpg");

        return imageFile;
    } // Tp create file image with date title

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            //------------Received image from camera in device serial number------------------------
            if (requestCode == CAMERA_DEVICE_SERIAL_NUMBER)
            {
                Uri picUri = pictureUri;
                startCropImageActivity(picUri, CROP_CAMERA_SERIAL_NUMBER);
                Toast.makeText(UserAreaActivity.this, "Serial Number Image Saved!", Toast.LENGTH_SHORT).show();
            }

            //---------------Cropped image from camera in device serial number----------------------
            if (requestCode == CROP_CAMERA_SERIAL_NUMBER)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Croppedimage(result, ivDeviceSerialNumber, etDeviceSerialNumber);
            }

            //------------Received image from gallery in device serial number-----------------------
            if (requestCode == GALLERY_DEVICE_SERIAL_NUMBER)
            {
                startCropImageActivity(data.getData(), CROP_GALLERY_SERIAL_NUMBER);
                Toast.makeText(UserAreaActivity.this, "Image from gallery", Toast.LENGTH_SHORT).show();
            }

            //--------------Cropped image from gallery in device serial number----------------------
            if(requestCode == CROP_GALLERY_SERIAL_NUMBER)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Croppedimage(result, ivDeviceSerialNumber, etDeviceSerialNumber);
            }

        }

    } // Received image and crop image from camera or gallery

    private void startCropImageActivity(Uri imageUri, int requestCode) {
        Intent vCropIntent = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .getIntent(this);

        startActivityForResult(vCropIntent, requestCode);
    } // To crop image from camera or gallery



    /* To set cropped image in image view and
     get text or number from image and set it
     in edit text.
     */
    public void Croppedimage(CropImage.ActivityResult result, ImageView iv, EditText et )
    {
        Uri resultUri = null; // get image uri
        if (result != null) {
            resultUri = result.getUri();
        }


        //set image to image view
        iv.setImageURI(resultUri);


        //get drawable bitmap for text recognition
        BitmapDrawable bitmapDrawable = (BitmapDrawable) iv.getDrawable();

        Bitmap bitmap = bitmapDrawable.getBitmap();

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!recognizer.isOperational())
        {
            Toast.makeText(this, "Error No Text To Recognize", Toast.LENGTH_LONG).show();
        }
        else
        {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder ab = new StringBuilder();

            //get text from ab until there is no text
            for(int i = 0 ; i < items.size(); i++)
            {
                TextBlock myItem = items.valueAt(i);
                ab.append(myItem.getValue());

            }

            //set text to edit text
            et.setText(ab.toString());
        }

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "-" + (month+1) + "-" + dayOfMonth;
        tvSelectedDate.setText(date);
        appSelectDate = date;

        Calendar time = Calendar.getInstance();
        hour = time.get(Calendar.HOUR_OF_DAY);
        minute = time.get(Calendar.MINUTE);
        second = time.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(UserAreaActivity.this, UserAreaActivity.this, hour, minute,DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    } // To get selected date and set it in text view then well open time picker

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay+":"+minute;
        tvSelectedTime.setText(time);
        appSelectTime = time;
    } // To get selected time and set it in text view

    private boolean checkEmptyEditText(EditText text, TextInputLayout TFB, String massage) {

        if (text.getText().toString().isEmpty())
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;

        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }

    } // To check empty edit text

    private boolean checkLengthDeviceSerialEditText (EditText text, TextInputLayout TFB, String massage) {
        if(text.getText().toString().length() > 10 || text.getText().toString().length() < 10)
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;
        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }


    } // To check length of device serial number

    private boolean checkLengthPlateNumber(EditText text, TextInputLayout TFB, String massage) {
        if(text.getText().toString().length() > 10)
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;
        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }
    } // To check length of plate number

    private boolean checkOdometerNumber(EditText text, TextInputLayout TFB, String massage) {
        if(text.getText().toString().length() > 8)
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;
        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }
    } // To check length of odometer number

    private boolean checkVehicleModel(EditText text, TextInputLayout TFB, String massage) {
        if(text.getText().toString().length() > 4 || text.getText().toString().length() < 4)
        {
            TFB.setErrorEnabled(true);
            TFB.setError(massage);
            return false;
        }
        else
        {
            TFB.setErrorEnabled(false);
            return true;
        }
    } // To check length of vehicle  model

    private boolean checkSpinner(String postion, MaterialSpinner sp, String massage) {
        if(postion.equals("Select Task Type") || postion.equals("Select Device Type") || postion.equals("Select Customer"))
        {

            sp.setError(massage);
            return false;
        }
        else
        {
            return true;
        }

    } // To check spinner selection

    private boolean checkGPS(double latitude, double longitude, String massage) {
        if(latitude == 0.0 && longitude == 0.0)
        {
            Toast.makeText(this, massage, Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            return true;
        }
    } // To check GPS number

    private void getTaskSpinner() {

        StringRequest stringRequest = new StringRequest("https://cardtest10.000webhostapp.com/Sync_Spinner_T.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArraytask = jsonResponse.getJSONArray("task");

                    for(int i=0; i < jsonArraytask.length(); i++)
                    {
                        JSONObject jsonObjecttask = jsonArraytask.getJSONObject(i);
                        String task = jsonObjecttask.optString("Type");
                        arrayTask.add(task);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                // Applying the adapter to our spinner
                spTaskType.setAdapter(new ArrayAdapter <>(UserAreaActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayTask));
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    } // To get task type table from json file and but it in array

    private void getDeviceSpinner() {

        StringRequest stringRequest = new StringRequest("https://cardtest10.000webhostapp.com/Sync_Spinner_D.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArraytask = jsonResponse.getJSONArray("device");

                    for(int i=0; i < jsonArraytask.length(); i++)
                    {
                        JSONObject jsonObjecttask = jsonArraytask.getJSONObject(i);
                        String task = jsonObjecttask.optString("Device_Type");
                        arrayDeviceType.add(task);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                // Applying the adapter to our spinner
                spDeviceModel.setAdapter(new ArrayAdapter <>(UserAreaActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayDeviceType));
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    } // To get device type table from json file and but it in array

    private void getCustomerSpinner() {

        StringRequest stringRequest = new StringRequest("https://cardtest10.000webhostapp.com/Sunc_Spinner_C.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArraytask = jsonResponse.getJSONArray("customer list");

                    for(int i=0; i < jsonArraytask.length(); i++)
                    {
                        JSONObject jsonObjecttask = jsonArraytask.getJSONObject(i);
                        String task = jsonObjecttask.optString("Custom");
                        arrayCustomer.add(task);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                // Applying the adapter to our spinner
                spCustomer.setAdapter(new ArrayAdapter<>(UserAreaActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayCustomer));
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserAreaActivity.this, error + "", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    } // To get customer table form json file and but it in array

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // To hide/close keyboard

}



