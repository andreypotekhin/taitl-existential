package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.logging.*;

public class NightCityStory
{
    private static final String OP = "/night-city/cats/save";
    private static final Logger LOGGER = Logger.getLogger(NightCityStory.class.getName());

    public static void main(String[] args)
    {
        try
        {
            run().forEach(outcome -> LOGGER.info(outcome));
        }
        catch (Exception failure)
        {
            throw new IllegalStateException("Night City workflow failed", failure);
        }
    }

    public static List<String> run() throws Exception
    {
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                Cat rejected = new Cat("Grey", "Park");
                Cat before = new Cat("Black", "Park");
                Cat after = new Cat("Black", "Garden");
                Map<Cat, Collection<Dwelling<Cat, ?>>> dwellings = new HashMap<>();
                dwellings.put(rejected, List.of(new Dwelling<Cat, Rugs>("Grey", "Park")));
                dwellings.put(before, List.of(new Dwelling<Cat, Rugs>("Grey", "Park")));
                dwellings.put(after, List.of(new Dwelling<Cat, Rugs>("Red", "Garden")));
                new ConfigureCats().configureLifecycle(OP, dwellings);

                List<String> outcomes = new ArrayList<>();
                rejectAndRollback(rejected);
                outcomes.add("Rejected a non-black cat and rolled back explicitly.");
                create(before);
                outcomes.add("Created a black cat with a dwelling.");
                relocate(before, after);
                outcomes.add("Relocated the cat to a dwelling while preserving its color.");
                return Collections.unmodifiableList(outcomes);
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    private static void rejectAndRollback(Cat cat) throws Exception
    {
        Tr tr = Ex.begin(OP);
        try
        {
            tr.create(cat);
            ValidationStageExceptions failure = null;
            try
            {
                tr.commit();
            }
            catch (ValidationStageExceptions rejected)
            {
                failure = rejected;
            }
            if (failure == null || failure.getCause() == null
                    || !failure.getCause().getMessage().contains("Cats are born black"))
            {
                throw new IllegalStateException("Expected the non-black cat rule to reject the operation");
            }
            tr.rollback();
        }
        finally
        {
            tr.close();
        }
    }

    private static void create(Cat cat) throws Exception
    {
        Tr tr = Ex.begin(OP);
        try
        {
            tr.create(cat);
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }

    private static void relocate(Cat before, Cat after) throws Exception
    {
        Tr tr = Ex.begin(OP);
        try
        {
            tr.transit(before, after);
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }
}
