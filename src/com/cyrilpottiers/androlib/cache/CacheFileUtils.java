package com.cyrilpottiers.androlib.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;

import com.cyrilpottiers.androlib.Log;
import com.cyrilpottiers.androlib.date.DateFormatter;

public class CacheFileUtils {
    private final static String         LOG            = "CacheFileUtils";

    private final static String         suffixe        = ".tmp";
    private final static String         suffixeDesc    = ".desc";
    private final static int            IO_BUFFER_SIZE = 8 * 1024;
    private final static int            MAX_FILE_SIZE  = 1024 * 1024;
    private final static CacheFileUtils singleton      = new CacheFileUtils();
//    private Context                     context        = null;
    private File                        cacheDir       = null;

    private CacheFileUtils() {
    }

    public static CacheFileUtils getInstance() {
        return singleton;
    }

    public static void instantiateCacheFile(Context context) {
//        singleton.context = context;
        singleton.cacheDir = context.getCacheDir();
    }

    public static void appendDebugFile(String name, String buffer) {
        if(!Log.isDebugging()) return;
        
        File file = new File(singleton.cacheDir, name);
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(DateFormatter.format(Calendar.getInstance().getTime()));
            bw.write('\n');
            bw.write(buffer);
            bw.write('\n');
            bw.close();
            fw.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeDebugFile(String name, String buffer) {
        if(!Log.isDebugging()) return;
        
        File file = new File(singleton.cacheDir, name);
        if(file.exists()) file.delete();
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(DateFormatter.format(Calendar.getInstance().getTime()));
            bw.write('\n');
            bw.write(buffer);
            bw.close();
            fw.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void appendBinDebugFile(String name, byte[] buffer) {
        if(!Log.isDebugging()) return;
        File file = new File(singleton.cacheDir, name);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(DateFormatter.format(Calendar.getInstance().getTime()).getBytes());
            fos.write('\n');
            fos.write(buffer);
            fos.write('\n');
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBinDebugFile(String name, byte[] buffer) {
        if(!Log.isDebugging()) return;
        File file = new File(singleton.cacheDir, name);
        if (file.exists()) file.delete();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(DateFormatter.format(Calendar.getInstance().getTime()).getBytes());
            fos.write('\n');
            fos.write(buffer);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readDebugFile(String name) {
        File file = new File(singleton.cacheDir, name);
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while (null != (line = br.readLine())) {
                sb.append(line).append('\n');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            br.close();
            fr.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean cacheURLFile(String uri, String id,
            HashMap<String, String> desc) {
        boolean succeed = true;

        Log.i(LOG, "cache pattern=" + uri);

        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixe).toString());
        File fileDesc = null;
        if (desc != null)
            fileDesc = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixeDesc).toString());

        try {
            HttpGet httpRequest = new HttpGet(uri);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[IO_BUFFER_SIZE];
            int read;
            while ((read = instream.read(b)) != -1) {
                fos.write(b, 0, read);
            }
            fos.flush();
            fos.close();
            instream.close();

            if (desc != null) {
                fileDesc.createNewFile();
                FileWriter fw = new FileWriter(fileDesc);
                BufferedWriter bw = new BufferedWriter(fw);

                String key, value;
                StringBuilder sb = new StringBuilder();
                Iterator<String> i = desc.keySet().iterator();
                while (i.hasNext()) {
                    sb.setLength(0);
                    key = i.next();
                    value = desc.get(key);
                    bw.write(sb.append(key).append('|').append(value).toString());
                    bw.newLine();
                }

                bw.close();
            }

        }
        catch (IOException e) {
            succeed = false;
            e.printStackTrace();
        }

        return succeed;
    }

    public static boolean cacheLocalFile(Bitmap bmp, String id,
            HashMap<String, String> desc) {
        boolean succeed = true;

        Log.i(LOG, "cache local");

        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixe).toString());
        File fileDesc = null;
        if (desc != null)
            fileDesc = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixeDesc).toString());

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            if (desc != null) {
                fileDesc.createNewFile();
                FileWriter fw = new FileWriter(fileDesc);
                BufferedWriter bw = new BufferedWriter(fw);

                String key, value;
                StringBuilder sb = new StringBuilder();
                Iterator<String> i = desc.keySet().iterator();
                while (i.hasNext()) {
                    sb.setLength(0);
                    key = i.next();
                    value = desc.get(key);
                    bw.write(sb.append(key).append('|').append(value).toString());
                    bw.newLine();
                }

                bw.close();
            }

        }
        catch (IOException e) {
            succeed = false;
        }

        return succeed;
    }

    public static boolean isCacheFileExist(String id) {
        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixe).toString());
        return file.exists();
    }

    public static String getCacheFilePath(String id) {
        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixe).toString());
        if (file.length() > MAX_FILE_SIZE) return null;
        return file.getPath();
    }

    public static String getCacheFileDescr(String id, String key) {
        String desc = null;
        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixeDesc).toString());
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String[] kv;
            while (true) {
                if ((line = br.readLine()) == null) break;
                kv = line.split("\\|");
                if (kv != null && kv.length == 2 && kv[0].equals(key)) {
                    desc = kv[1];
                    break;
                }
            }
        }
        catch (IOException e) {
        }

        return desc;
    }

    public static void clearCacheFile(String id) {
        File file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixe).toString());
        file.delete();
        file = new File(singleton.cacheDir, new StringBuilder().append(id).append(CacheFileUtils.suffixeDesc).toString());
        file.delete();
    }

}
