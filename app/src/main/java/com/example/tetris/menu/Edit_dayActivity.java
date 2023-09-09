package com.example.tetris.menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tetris.R;
import com.example.tetris.dao.UsersDayDao;
import com.example.tetris.db.DataBase;
import com.example.tetris.entity.UsersDay;
import com.example.tetris.function.Function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Edit_dayActivity extends AppCompatActivity {

    private EditText titleCreateEditText;
    private ImageView imageOk;
    private ImageView imageImageView;
    private ImageView photographImageView;
    private TextView textView;
    private EditText contentEditText;
    private String receivedUsername;
    private DataBase db;
    private UsersDayDao usersDayDao;
    private byte[] image;
    private ArrayList<byte[]> images;
    private byte[] graph;
    private String title;
    private String content;
    private static final int REQUEST_IMAGE_PICK = 1;
    private String currentTime;
    private String time;
    private UsersDay usersday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createday);
        init();
    }

    private void init() {
        titleCreateEditText = findViewById(R.id.title_create);
        imageOk = findViewById(R.id.image_ok);
        textView = findViewById(R.id.textView);
        contentEditText = findViewById(R.id.content);
        imageImageView = findViewById(R.id.imageView3);
        photographImageView = findViewById(R.id.imageView2);

        // 获取传递过来的Username
        receivedUsername = getIntent().getStringExtra("Username");
        time = getIntent().getStringExtra("Date");

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 创建一个格式化时间的对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用格式化对象将时间转换为字符串
        currentTime = dateFormat.format(calendar.getTime());
        // 在TextView中显示当前时间
        textView.setText(currentTime);

        //数据库初始化
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "mydb")
//                       .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        usersDayDao = db.usersDayDao();


        initTextAndImage();
        //EditText初始文字


        imageOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = titleCreateEditText.getText().toString();
                content = contentEditText.getText().toString();
                if (images.size() >= 3) {
                    images.subList(3, images.size()).clear();
                    Toast.makeText(Edit_dayActivity.this, "最多存在3张图片!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("Edit", "edit");
                    setResult(RESULT_OK, intent);
                    if (save()) {
                        Toast.makeText(Edit_dayActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Edit_dayActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
//        imageImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                openGallery();
//            }
//        });

        setImage();
    }

    /**
     * 初始化文字和图片
     */
    private void initTextAndImage() {
        usersday = new UsersDay();
        usersday = usersDayDao.getDiaryByUsernameAndTime(receivedUsername, time);
        images = Function.StringToList(usersday.getImageData());
        if (usersday != null) {
            titleCreateEditText.setText(usersday.getTitle());

            String inputString = usersday.getContent();
            String target = "[image]";
            int startIndex = 0;
            List<Integer> positions = new ArrayList<>();

            while (startIndex < inputString.length()) {
                int foundIndex = inputString.indexOf(target, startIndex);
                if (foundIndex != -1) {

                    positions.add(foundIndex + target.length() - 1);
                    startIndex = foundIndex + 1;
                } else {
                    break;
                }
            }
            StringBuilder stringBuilder = new StringBuilder(inputString);
            int change = 0;
            for (int index : positions
            ) {
                if (inputString == null || index < 0 || index >= inputString.length()) {
                    // 处理无效输入，或索引超出字符串长度的情况

                } else {
                    Log.d("decode", " " + stringBuilder);
                    stringBuilder.deleteCharAt(index - change);
                    change++;
                }
            }
            inputString = stringBuilder.toString();
            Log.d("decode", " " + inputString);
            contentEditText.setText(inputString);
            for (int i = 0; i < positions.size(); i++) {
                if (i>images.size()-1) break;
                Bitmap bitmap = BitmapFactory.decodeByteArray(images.get(i), 0, images.get(i).length);
                insertImage(bitmap, positions.get(i));
//                Log.d("decode", "initTextAndImage: " + inputString.charAt(positions.get(i)-1));
            }
            Log.d("decode", "initTextAndImage: " + contentEditText.getText());

        } else Log.d("flag", "init: 2222");
    }


    /**
     * 相册点击
     */
    private void setImage() {
        // 通过按钮点击事件启动相册应用
        imageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        // 处理相册应用返回的结果
        contentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    SpannableStringBuilder builder = (SpannableStringBuilder) contentEditText.getText();
                    ImageSpan[] imageSpans = builder.getSpans(0, builder.length(), ImageSpan.class);
                    int cursorPosition = contentEditText.getSelectionStart();

                    for (ImageSpan span : imageSpans) {
                        int start = builder.getSpanStart(span);
                        int end = builder.getSpanEnd(span);

                        if (cursorPosition >= start && cursorPosition <= end) {
                            // 删除对应的ImageSpan
                            builder.replace(start, end, "");
                            builder.removeSpan(span);
                            contentEditText.setText(builder);
                            contentEditText.setSelection(start); // 恢复光标位置
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // 使用选中的图片URI进行处理，显示在ImageView
            try {
                // 将URI转换为Bitmap对象
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                //将图片转成byte[]
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                image = byteArrayOutputStream.toByteArray();
                images.add(image);

                // 获取EditText的当前光标位置
                int selectionStart = contentEditText.getSelectionStart();
                InsertImage(bitmap, selectionStart);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 初始化式插入图片
     *
     * @param bitmap
     */
    private void insertImage(Bitmap bitmap, int selectionStart) {
        // 创建一个可编辑的SpannableStringBuilder对象，并将图片插入其中
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // 压缩图片

        // 调整图片大小
        int desiredWidth = 200; // 设置你想要的图片宽度
        int desiredHeight = 200; // 设置你想要的图片高度
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);

        // 将调整大小后的Bitmap对象转换为Drawable对象
        Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);

        // 创建ImageSpan对象，并将Drawable设置为可删除
        ImageSpan imageSpan = new ImageSpan(drawable);

        // 在可编辑的SpannableStringBuilder对象中插入ImageSpan
        builder.append("]");
        builder.setSpan(imageSpan, builder.length() - 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());


        // 在当前光标位置插入图片
        Editable editable = contentEditText.getText();
        editable.insert(selectionStart, builder);
    }

    /**
     * 在光标处插入图片
     *
     * @param bitmap
     */
    private void InsertImage(Bitmap bitmap, int selectionStart) {
        // 创建一个可编辑的SpannableStringBuilder对象，并将图片插入其中
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // 压缩图片
        Bitmap compressedBitmap = compressImage(bitmap);

        // 调整图片大小
        int desiredWidth = 200; // 设置你想要的图片宽度
        int desiredHeight = 200; // 设置你想要的图片高度
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(compressedBitmap, desiredWidth, desiredHeight, false);

        // 将调整大小后的Bitmap对象转换为Drawable对象
        Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);

        // 创建ImageSpan对象，并将Drawable设置为可删除
        ImageSpan imageSpan = new ImageSpan(drawable);

        // 在可编辑的SpannableStringBuilder对象中插入ImageSpan
        builder.append("[image]");
        builder.setSpan(imageSpan, builder.length() - 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());


        // 在当前光标位置插入图片
        Editable editable = contentEditText.getText();
        editable.insert(selectionStart, builder);
    }


    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);  // 压缩质量为80%
        byte[] compressedData = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }
//
//    /**
//     * 进入相册选择照片
//     */
//    private void openGallery() {
//
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
//            Uri selectedImageUri = data.getData();
//            // 在EditText中插入照片
//            Log.d("flag", "onActivityResult: " + resultCode + "");
//            insertImageIntoEditText(selectedImageUri);
//
//
//        }
//    }
//
//    /**
//     * 将照片插入文本中
//     *
//     * @param selectedImageUri
//     */
//    private void insertImageIntoEditText(Uri selectedImageUri) {
//        try {
//            // 从Uri中获取Bitmap
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//            Log.d("flag", "onActivityResult111111: " + bitmap.toString());
//
//            //将图片转成byte[]
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            image = byteArrayOutputStream.toByteArray();
//
//            // 将Bitmap插入到EditText中
//
//            SpannableStringBuilder builder = new SpannableStringBuilder(contentEditText.getText());
//
//            builder.append("\n"); // 在照片前添加换行符
//            int start = builder.length();
////            builder.setSpan(new ImageSpan(CreateDayActivity.this, bitmap), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            Log.d("flag", "onActivityResult1www11111: ");
//            // 更新EditText的内容
//            contentEditText.setText(builder);
//            contentEditText.setSelection(builder.length()); // 将光标移动到文本末尾
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 将日记保存，并写入数据库
     */
    private boolean save() {

        usersday.setTitle(title);
//        usersday.setUsername(receivedUsername);

        usersday.setContent(content);
//        usersday.setCreateTime(time);

        String string = Function.listToString(images);
        usersday.setImageData(string);
        if (usersday != null) {

            usersDayDao.updateDiary(usersday);
            return true;
        }
        return false;
    }
}