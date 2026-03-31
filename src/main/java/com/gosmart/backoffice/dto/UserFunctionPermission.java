package com.gosmart.backoffice.dto;

public class UserFunctionPermission {
    private final boolean create;
    private final boolean edit;
    private final boolean delete;
    private final boolean view;

    public UserFunctionPermission(boolean create, boolean edit, boolean delete, boolean view) {
        this.create = create;
        this.edit = edit;
        this.delete = delete;
        this.view = view;
    }

    public boolean isCreate() {
        return create;
    }

    public boolean isEdit() {
        return edit;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isView() {
        return view;
    }
}
