package com.example.soundroid;

import android.net.Uri;

public class Audio {
        public final Uri uri;
        public final String name;
        public final int duration;
        public final int size;

        public Audio(Uri uri, String name, int duration, int size) {
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }

        @Override
        public String toString() {
            return "uri : " + uri + ", name : " + name + ", duration : " + duration + ", size : " + size;
        }
}
