package com.jobma.employer.model.subAccounts;

public class AddPermissionDialog {

    private boolean isChecked;
    private String permission;
    private String addedPermission;

    public AddPermissionDialog(boolean isChecked, String permission, String addedPermission) {
        this.isChecked = isChecked;
        this.permission = permission;
        this.addedPermission = addedPermission;
    }

    public String getAddedPermission() {
        return addedPermission;
    }

    public void setAddedPermission(String addedPermission) {
        this.addedPermission = addedPermission;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

}
