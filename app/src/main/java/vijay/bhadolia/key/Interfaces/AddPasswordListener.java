package vijay.bhadolia.key.Interfaces;

import vijay.bhadolia.key.model.Password;

public interface AddPasswordListener {
    void onSaveButtonClicked(Password password, boolean newPassword);
}
