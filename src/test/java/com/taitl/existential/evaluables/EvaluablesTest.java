package com.taitl.existential.evaluables;

import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class EvaluablesTest
{
    static class NamedEv implements Ev<String>
    {
        final String name;

        NamedEv(String name)
        {
            this.name = name;
        }
    }

    static class NamedEvs implements Evs<String>
    {
        final List<Ev<String>> evs = new ArrayList<>();
        final TypeKey<String> typeKey = new TypeKey<>(String.class);

        public List<Ev<String>> list()
        {
            return evs;
        }

        public Evs<String> add(Ev<String> ev)
        {
            evs.add(ev);
            return this;
        }

        public TypeKey<String> typeKey()
        {
            return typeKey;
        }
    }

    static class NamedEvaluable implements Evaluable
    {
        final List<Evs<?>> evs;

        NamedEvaluable(List<Evs<?>> evs)
        {
            this.evs = evs;
        }

        public List<Evs<?>> evs()
        {
            return evs;
        }
    }

    static class RecordingEvaluator implements Evaluator
    {
        final List<Ev<?>> visited = new ArrayList<>();

        public <T> void visit(Ev<T> ev)
        {
            visited.add(ev);
        }
    }

    @Nested
    class SingleFlags
    {
        @Test
        @DisplayName("Default single flags reflect evs nature")
        void reflectEvsNature()
        {
            assertThat(new NamedEv("one").single(), is(true));
            assertThat(new NamedEvs().single(), is(false));
        }
    }

    @Nested
    class EvaluatorTraversal
    {
        @Test
        @DisplayName("Evaluator traverses evs in order")
        void evsInOrder()
        {
            NamedEv first = new NamedEv("first");
            NamedEv second = new NamedEv("second");
            NamedEvs evs = new NamedEvs();
            evs.add(first).add(second);

            RecordingEvaluator evaluator = new RecordingEvaluator();

            evs.accept(evaluator);

            assertThat(evaluator.visited, contains(first, second));
        }

        @Test
        @DisplayName("Evaluator traverses evaluable in order")
        void evaluableInOrder()
        {
            NamedEv first = new NamedEv("first");
            NamedEv second = new NamedEv("second");
            NamedEv third = new NamedEv("third");
            NamedEvs evsA = new NamedEvs();
            NamedEvs evsB = new NamedEvs();
            evsA.add(first).add(second);
            evsB.add(third);

            RecordingEvaluator evaluator = new RecordingEvaluator();
            NamedEvaluable evaluable = new NamedEvaluable(List.of(evsA, evsB));

            evaluable.accept(evaluator);

            assertThat(evaluator.visited, contains(first, second, third));
        }
    }
}
