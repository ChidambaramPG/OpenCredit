package chidhu.opencredit;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chidhu.opencredit.databaseclasses.Customers;
import chidhu.opencredit.databaseclasses.OpenCreditDatabase;
import chidhu.opencredit.databaseclasses.Transactions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView uname, numbr;
    ImageView agremnt, id, remove;

    boolean permissionsObtnd = false;

    int GALLERY_CODE = 0;
    int CAMERA_CODE = 1;

    String username,number,itemType;

    public OpenCreditDatabase opDB;
    Customers cust;
    Date c;

    RecyclerView customerTransacionList;
    RecyclerView.Adapter adapter;

    List<Transactions> customerTransactions;
    List<CustomerTransaction> csts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        c = Calendar.getInstance().getTime();

        opDB = Room.databaseBuilder(this,OpenCreditDatabase.class,"OpenCreditDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        uname = findViewById(R.id.usernameTxt);
        numbr = findViewById(R.id.yearTxt);
        agremnt = findViewById(R.id.agrementScnStat);
        id = findViewById(R.id.idProofScnStat);
        remove = findViewById(R.id.removeUserBtn);

        agremnt.setOnClickListener(this);
        id.setOnClickListener(this);
        remove.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsObtnd = false;
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
        }

        Intent customerDetails = getIntent();
        Bundle b = customerDetails.getExtras();
        if(b!=null)
        {
            username =(String) b.get("name");
            number = (String) b.get("number");
            uname.setText(username);
            numbr.setText(number);
            System.out.println(username+" ("+number+")");
        }
        cust = opDB.openCreditDAO().getUserByContactId(number);
        if(cust.getAgreement()!=null && cust.getAgreement().equals("true")){
            agremnt.setImageResource(R.drawable.ic_debit_icon);
        }
        if(cust.getIdproof()!=null && cust.getIdproof().equals("true")){
            id.setImageResource(R.drawable.ic_debit_icon);
        }

        customerTransactions = opDB.openCreditDAO().getUserTransactions(number);
        customerTransacionList = findViewById(R.id.customerTransacionList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        customerTransacionList.setLayoutManager(mLayoutManager);
        adapter = new CustomerProfileAdapter(customerTransactions,this);
        customerTransacionList.setAdapter(adapter);

        int credit = 0, debit =0,bal;

        for(Transactions t : customerTransactions){
            if(t.getTransType().equals("credit")){
                credit += Integer.valueOf(t.getAmount());
            }
            if(t.getTransType().equals("debit")){
                debit += Integer.valueOf(t.getAmount());
            }
        }
        bal = credit-debit;
        System.out.println(credit + "," + debit + "," + bal);
        SpannableString s = new SpannableString("Rs."+String.valueOf(bal));
        s.setSpan(new chidhu.opencredit.TypefaceSpan(this, "Oswald-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionsObtnd = true;
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.agrementScnStat:{

                itemType = "agreement";
                showPictureDialog();

                break;
            }
            case R.id.idProofScnStat:{

                itemType = "idproof";
                showPictureDialog();

                break;
            }
            case R.id.removeUserBtn:{

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to remove "+ username+" ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                opDB.openCreditDAO().deleteByUserContactId(number);
                                dialog.cancel();
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();

                break;
            }
            default:{

            }

        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    private void takePhotoFromCamera() {

        Toast.makeText(this, "Camera upload not setup yet! ", Toast.LENGTH_SHORT).show();
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "chidhu.opencredit",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, CAMERA_CODE);
//            }
//        }
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, CAMERA_CODE);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_CODE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA_CODE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

//            saveImage(thumbnail);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/OpenCredit/UserDocuments/"+number);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, itemType  + ".png");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            System.out.println("itemType: "+itemType);



            if(itemType.equals("agreement")){
                agremnt.setImageResource(R.drawable.ic_debit_icon);
                cust.setAgreement("true");
            }

            if(itemType.equals("idproof")){
                id.setImageResource(R.drawable.ic_debit_icon);
                cust.setIdproof("true");
            }
            opDB.openCreditDAO().updateCustomer(cust);

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return "";
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {

        String imageFileName = itemType;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.debit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_debit) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("RECEIVE PAYMENT");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("RECEIVE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Transactions newTrans = new Transactions();
                    SimpleDateFormat trnsDt = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedTrnsDate = trnsDt.format(c);
                    SimpleDateFormat trnsTi = new SimpleDateFormat("hh-mm");
                    String formattedTrnsTime = trnsTi.format(c);
                    SimpleDateFormat trnsMonth = new SimpleDateFormat("MMM");
                    String formattedTrnsMonth = trnsMonth.format(c);
                    SimpleDateFormat trnsYear = new SimpleDateFormat("yyyy");
                    String formattedTrnsYear = trnsYear.format(c);

                    newTrans.setUid(username + number);
                    newTrans.setTime(formattedTrnsTime);
                    newTrans.setDate(formattedTrnsDate);
                    newTrans.setMonth(formattedTrnsMonth);
                    newTrans.setYear(formattedTrnsYear);
                    newTrans.setNumber(number);
                    newTrans.setUname(username);
                    newTrans.setAmount(input.getText().toString());

                    newTrans.setTransType("debit");
                    opDB.openCreditDAO().addTransaction(newTrans);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }

        return true;
    }



}
