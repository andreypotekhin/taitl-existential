package com.taitl.ex.logic.unused.indexes;

import java.util.*;

@Deprecated
public class ConfiguredEventTypes
{
    Set<String> eventTypes = new LinkedHashSet<>();

    // void add(EventType eventType)
    // {
    // sane(eventType, "eventType");
    // if (!eventTypes.contains(eventType.toString()))
    // {
    // eventTypes.add(eventType.toString());
    // // TODO: add generic and elementary versions of the event key,
    // // e.g. for "ReadAndLock<Doc<JSON>>" also add "ReadAndLock<Doc>", "ReadAndLock",
    // // "Read<Doc<JSON>>", "Read<Doc>", "Update"
    // }
    // }
    //
    // boolean contains(EventType eventType)
    // {
    // sane(eventKey, "eventKey");
    // return eventKeys.contains(eventKey.toString());
    // }
}
