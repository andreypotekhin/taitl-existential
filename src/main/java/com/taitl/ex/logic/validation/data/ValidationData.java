package com.taitl.ex.logic.validation.data;

import java.util.*;
import com.taitl.existential.transactions.*;

public class ValidationData
{
    Tr tr;
    EventKeys eventKeys;
    BitSet eventTypeMask = new BitSet(64);

    public ValidationData(Tr tr)
    {
        this.tr = tr;
        this.eventKeys = new EventKeys(tr);
    }

    public EventKeys eventKeys()
    {
        return eventKeys;
    }

    public BitSet eventTypeMask()
    {
        return eventTypeMask;
    }

    public void close()
    {
        tr = null;
        eventKeys = null;
        eventTypeMask = null;
    }
}
