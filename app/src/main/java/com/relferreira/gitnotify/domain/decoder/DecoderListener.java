package com.relferreira.gitnotify.domain.decoder;

import java.util.List;

/**
 * Created by relferreira on 2/11/17.
 */

public interface DecoderListener {
    void successLoadingData(List items);
    void errorLoadingData(String error);
    void showPageLoading(boolean status);
}
