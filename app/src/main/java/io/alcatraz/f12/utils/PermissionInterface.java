package io.alcatraz.f12.utils;

public interface PermissionInterface {
    void onResult(int requestCode, String[] permissions, int[] granted);
}
