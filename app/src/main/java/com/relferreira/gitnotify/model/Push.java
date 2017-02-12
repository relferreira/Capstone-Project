package com.relferreira.gitnotify.model;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created by relferreira on 2/12/17.
 */
@Value.Immutable
public abstract class Push {
    public abstract Integer size();
    public abstract List<Commit> commits();
}
