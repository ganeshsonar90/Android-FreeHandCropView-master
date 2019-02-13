package jp.itnav.freehandcropsample.paint.text.type.Model;

import java.util.ArrayList;

public  class PhotoEntry {
    public int bucketId;
    public int imageId;
    public long dateTaken;
    public int duration;
    public String path;
    public int orientation;
    public String thumbPath;
    public String imagePath;
    public CharSequence caption;
    public boolean isFiltered;
    public boolean isPainted;
    public boolean isCropped;
    public boolean isMuted;
    public int ttl;
    public boolean canDeleteAfter;

    public PhotoEntry(int bucketId, int imageId, long dateTaken, String path, int orientation) {
        this.bucketId = bucketId;
        this.imageId = imageId;
        this.dateTaken = dateTaken;
        this.path = path;
            this.orientation = orientation;
    }

    public PhotoEntry() {
    }

    public void reset() {
        isFiltered = false;
        isPainted = false;
        isCropped = false;
        ttl = 0;
        imagePath = null;
            thumbPath = null;

        caption = null;
    }
}
