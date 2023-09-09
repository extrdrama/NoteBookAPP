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
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CreateDayActivity extends AppCompatActivity {

    private EditText titleCreateEditText;
    private ImageView imageOk;
    private ImageView imageImageView;
    private ImageView photographImageView;
    private TextView textView;
    private LinearLayout imageContainer;
    private EditText contentEditText;
    private String receivedUsername;
    private DataBase db;
    private UsersDayDao usersDayDao;
    private byte[] image;
    private ArrayList<byte[]> images;
    private byte[] graph;
    private String title;
    private String content;

    private String currentTime;
    private Intent intent;

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
//        imageContainer = findViewById(R.id.imageContainer);

        images = new ArrayList<>();

        // 获取传递过来的Username
        receivedUsername = getIntent().getStringExtra("Username");

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 创建一个格式化时间的对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用格式化对象将时间转换为字符串
        currentTime = dateFormat.format(calendar.getTime());
        // 在TextView中显示当前时间
        textView.setText(currentTime);

        title = titleCreateEditText.getText().toString();
        content = contentEditText.getText().toString();


        //数据库初始化
        db = Room.databaseBuilder(getApplicationContext(), DataBase.class, "mydb")
//                       .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        usersDayDao = db.usersDayDao();

        imageOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = titleCreateEditText.getText().toString();
                content = contentEditText.getText().toString();

                if (images.size() > 3) {
                    images.subList(3, images.size()).clear();


                    Log.d("decode", "onClick: "+images.size());
                    Toast.makeText(CreateDayActivity.this, "最多存在三张图片!", Toast.LENGTH_SHORT).show();
                } else {
//                    content=deleteOther();
                    intent = new Intent();
                    setResult(RESULT_OK, intent);
                    if (save()) {
                        Toast.makeText(CreateDayActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateDayActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
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
//    /**
//     * 仅保留前n个“content”
//     */
//    private String deleteOther() {
//        int count = 0;
//        int index = 0;
//        String pattern = "[image]";
//        StringBuilder result = new StringBuilder();
//
//        if (images.size()>3) {
//            while ((index = content.indexOf(pattern, index)) != -1) {
//                if (count < 3) {
//                    result.append(content.substring(0, index + pattern.length()));
//                    content = content.substring(index + pattern.length());
//                    count++;
//                } else {
//                    content = content.substring(index + pattern.length());
//                }
//            }
//        }
//        result.append(content);
//        Log.d("decode", "deleteOther: "+result);
//        return  result.toString();
//
//    }




    /**
     * 点击相册
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                image = byteArrayOutputStream.toByteArray();
                images.add(image);

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
                // 获取EditText的当前光标位置
                int selectionStart = contentEditText.getSelectionStart();

                // 在当前光标位置插入图片
                Editable editable = contentEditText.getText();
                editable.insert(selectionStart, builder);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);  // 压缩质量为20%
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
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, 1);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
//            Uri selectedImageUri = data.getData();
//            // 在EditText中插入照片
//            Log.d("flag", "onActivityResult: " + resultCode + "");
//            insertImageIntoEditText(selectedImageUri);
//
//
//        }
//    }

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
//            // 压缩图片
//            Bitmap compressedBitmap = compressImage(bitmap);
//            // 调整图片大小
//            int desiredWidth = 200; // 设置你想要的图片宽度
//            int desiredHeight = 200; // 设置你想要的图片高度
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(compressedBitmap, desiredWidth, desiredHeight, false);
//            // 将调整大小后的Bitmap对象转换为Drawable对象
//            Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);
//            // 创建ImageSpan对象，并将Drawable设置为可删除
//            ImageSpan imageSpan = new ImageSpan(drawable);
//// 在可编辑的SpannableStringBuilder对象中插入ImageSpan
//            builder.append(" ");
//            builder.setSpan(imageSpan, builder.length() - 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            // 获取EditText的当前光标位置
//            int selectionStart = content.getSelectionStart();
//
//            // 在当前光标位置插入图片
//            Editable editable = content.getText();
//            editable.insert(selectionStart,builder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 将照片插入文本中
//     *
//     * @param selectedImageUri
//     */
//    private void insertCompressedImageIntoEditText(Uri selectedImageUri) {
//        try {
//            // 从Uri中获取Bitmap
//            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//            Log.d("TAG", "insertCompressedImageIntoEditText: "+imageBitmap);
//            // 创建一个ImageView并设置图片
//            ImageView imageView = new ImageView(CreateDayActivity.this);
//            int maxWidth = 20; // 限制的最大宽度
//            int maxHeight = 20; // 限制的最大高度
//
//// 获取原始图片的宽度和高度
//            int originalWidth = imageBitmap.getWidth();
//            int originalHeight = imageBitmap.getHeight();
//
//// 计算缩放比例，确保宽度和高度都不超过限制
//            float widthScale = (float) maxWidth / originalWidth;
//            float heightScale = (float) maxHeight / originalHeight;
//            float scaleFactor = Math.min(widthScale, heightScale);
//
//// 设置 ImageView 的布局参数，限制大小
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    (int) (originalWidth * scaleFactor),
//                    (int) (originalHeight * scaleFactor)
//            );
//            Log.d("flag", "insertCompressedImageIntoEditText: "+(int) (originalWidth * scaleFactor));
//            imageView.setLayoutParams(layoutParams);
//
//
//            imageView.setImageBitmap(imageBitmap);
//
//            // 将ImageView添加到LinearLayout中
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            imageContainer.addView(imageView, params);
//
//            // 在EditText中插入文本或占位符
//            Editable editable = contentEditText.getText();
//            int cursorPosition = contentEditText.getSelectionStart();
//            editable.insert(cursorPosition, "[Image]");

//            // 监听EditText的文本变化
//            contentEditText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    // 处理特殊标记，如"[Image]"，并将其替换为图片
//                    String text = s.toString();
//                    if (text.contains("[Image]")) {
//                        int startIndex = text.indexOf("[Image]");
//                        int endIndex = startIndex + "[Image]".length();
//
//                        // 从资源中加载图片
//
//
//                        // 创建一个ImageSpan，将图片插入到EditText中
//                        SpannableStringBuilder builder = new SpannableStringBuilder(text);
//                        builder.replace(startIndex, endIndex, ""); // 删除占位符
//                        builder.setSpan(new ImageSpan(CreateDayActivity.this, imageBitmap), startIndex, startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                        // 更新EditText的内容
//                        contentEditText.setText(builder);
//                        contentEditText.setSelection(endIndex - "[Image]".length());
//                    }
//                }
//            });

//            // 压缩图片
//            Bitmap compressedBitmap = compressBitmap(originalBitmap, 800); // 800表示压缩后图片的最大宽度
//
//            // 添加一个空行
//            SpannableStringBuilder builder = new SpannableStringBuilder(contentEditText.getText());
//            builder.append("\n"); // 在照片前添加换行符
//
//            // 获取新的文本长度
//            int start = builder.length();
//
//            // 插入压缩后的图片
//            builder.setSpan(new ImageSpan(CreateDayActivity.this, compressedBitmap), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            // 更新EditText的内容
//            contentEditText.setText(builder);
//            contentEditText.setSelection(builder.length()); // 将光标移动到文本末尾
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//private Bitmap compressImage(Bitmap image) {
//    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//    image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);  // 压缩质量为80%
//    byte[] compressedData = outputStream.toByteArray();
//    return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
//}

    /**
     * 将日记保存，并写入数据库
     */
    private boolean save() {
        UsersDay usersDay = new UsersDay();
        usersDay.setTitle(title);
        usersDay.setUsername(receivedUsername);

        usersDay.setContent(content);
        usersDay.setCreateTime(currentTime);

        String string = Function.listToString(images);
        usersDay.setImageData(string);

//        intent.putExtra("UsersDay", usersDay);
        if (receivedUsername != null && currentTime != null) {
            usersDayDao.insertDiary(usersDay);
            return true;
        }
        return false;
    }
}