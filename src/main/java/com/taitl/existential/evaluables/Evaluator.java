package com.taitl.existential.evaluables;

public interface Evaluator
{
    <T> void visit(Ev<T> ev);
}
