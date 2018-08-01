package com.android.papers.qmkl_android.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.papers.qmkl_android.model.DownloadedFile;
import com.android.papers.qmkl_android.model.PaperFile;
import com.android.papers.qmkl_android.util.PaperFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16/4/25.
 * Alter by fxz on 18/7/25
 */
public class DownloadDB extends SQLiteOpenHelper {
    private volatile static DownloadDB downloadDB;

    private SQLiteDatabase db;

    private static final String TABLE_NAME = "download_info";//表名
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COURSE = "course";
    private static final String SIZE = "size";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String TIME = "time";
    private static final String PATH = "path";

    //根据PATH查找文件所有信息
    private static final String QUERY_DOWNLOADED = "SELECT * FROM " + TABLE_NAME
            + " WHERE " + PATH + " = ?";
    //按照（文件名，课程，大小，类型，URL，时间）插入数据
    private static final String ADD_DOWNLOADED = "INSERT INTO " + TABLE_NAME
            + "(" + NAME + ", " + COURSE + ", " + SIZE + ", " + TYPE + ", "
            + URL + ", " + TIME + ", " + PATH +") "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    //根据PATH删除该文件所有信息
    private static final String REMOVE_DOWNLOADED = "DELETE FROM " + TABLE_NAME + " WHERE "
            + PATH + " = ?";
    //搜索数据库中的所有文件
    private static final String EMPTY_QUERY = "SELECT * FROM " + TABLE_NAME;
    //降序搜索所有文件
    private static final String GET_DOWNLOADED = EMPTY_QUERY + " ORDER BY "
            + ID + " DESC";
    //根据PATH搜索文件名(担心文件重复下载，所以在数据库内查找而不是直接截取末尾文件名)
    private static final String QUERY_NAME = "SELECT " + NAME + " from " + TABLE_NAME + " WHERE "
            + PATH + " = ?";

    private DownloadDB(Context context) {
        super(context, "Downloaded_Info.db", null, 1);
        db = getWritableDatabase();
    }

    public static DownloadDB getInstance(Context context) {
        if (downloadDB == null) {
            synchronized (DownloadDB.class) {
                if (downloadDB == null) {
                    downloadDB = new DownloadDB(context);
                }
            }
        }
        return downloadDB;
    }

    //创建数据库，主码为ID，其他非空
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME +" TEXT NOT NULL,"
                + COURSE +" TEXT NOT NULL,"
                + SIZE + " TEXT NOT NULL,"
                + TYPE + " TEXT NOT NULL,"
                + URL + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL,"
                + PATH + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //判断该path的文件是否已下载
    public boolean isDownloaded(String path) {
        Cursor cursor = db.rawQuery(QUERY_DOWNLOADED, new String[]{ path });
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    //添加下载信息
    public void addDownloadInfo(PaperFile paperFile) {
        db.execSQL(ADD_DOWNLOADED, new String[] {
                paperFile.getName(),
                paperFile.getCourse(),
                paperFile.getSize(),
                paperFile.getType(),
                paperFile.getUrl(),
                PaperFileUtils.getCurrentTime(),
                paperFile.getPath()
        });
    }

    //删除下载信息
    public void removeDownloadInfo(String path) {
        db.execSQL(REMOVE_DOWNLOADED, new String[] { path });
    }

    //已下载文件是空的则返回true
    public boolean isEmpty() {
        Cursor cursor = db.rawQuery(GET_DOWNLOADED, null);
        boolean result = !cursor.moveToFirst();
        cursor.close();
        return result;
    }

    /**
     *      获取已下载的文件
     *
     * @return 已下载的文件
     */
    public List<DownloadedFile> getDownloadedFiles() {
        ArrayList<DownloadedFile> downloadedFiles = new ArrayList<>();

        Cursor cursor = db.rawQuery(GET_DOWNLOADED, null);

        while (cursor.moveToNext()) {

            DownloadedFile downloadedFile = new DownloadedFile();

            PaperFile file = new PaperFile();
            file.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            file.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
            file.setCourse(cursor.getString(cursor.getColumnIndex(COURSE)));
            file.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
            file.setSize(cursor.getString(cursor.getColumnIndex(SIZE)));
            file.setDownload(true);
            file.setPath(cursor.getString(cursor.getColumnIndex(PATH)));

            downloadedFile.setFile(file);
            downloadedFile.setDownloadTime(cursor.getString(cursor.getColumnIndex(TIME)));

            downloadedFiles.add(downloadedFile);
        }
        cursor.close();

        return downloadedFiles;
    }

    public String getFileName(String path) {
        String result = "";

        Cursor cursor = db.rawQuery(QUERY_NAME, new String[]{ path });
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }
        cursor.close();

        return result;
    }

    /**
     * 为下载文件适当添加后缀以确定下载文件的储存名唯一
     *
     * @param name
     * @return
     */
    public static String addSuffix(String name, int suffix) {
        StringBuffer buffer = new StringBuffer(name);
        int index = buffer.lastIndexOf(".");
        return buffer.replace(index, index + 1, "(" + suffix + ").").toString();
    }

}
