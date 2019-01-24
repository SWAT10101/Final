package com.example.swat.axistechnical;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserAreaRequest  extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://cardtest10.000webhostapp.com/Send_Data.php";

    private Map<String , String> params;

    UserAreaRequest(int id,
                    String deviceserialnumber,
                    String vehicleplatenumber,
                    String companyname,
                    String odometer,
                    String fleetid,
                    String tasktype,
                    String devicemodel,
                    String vehiclemake,
                    int vehiclemodel,
                    double latitude,
                    double longitude,
                    String currentdate,
                    String currenttime,
                    Response.Listener<String> listener)
    {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("tec_ID", id + "");
        params.put("deviceserialnumber" , deviceserialnumber);
        params.put("vehicleplatenumber" , vehicleplatenumber);
        params.put("companyname" , companyname);
        params.put("odometer" , odometer);
        params.put("tasktype" , tasktype);
        params.put("devicemodel" , devicemodel);
        params.put("vehiclemake" , vehiclemake);
        params.put("vehiclemodel" , vehiclemodel + "");
        params.put("latitude" , latitude + "");
        params.put("longitude" , longitude + "");
        params.put("currentdate" , currentdate);
        params.put("currenttime" , currenttime);
        params.put("fleetid", fleetid);

    }


    @Override
    public Map<String , String> getParams()
    {
        return params;
    }
}
