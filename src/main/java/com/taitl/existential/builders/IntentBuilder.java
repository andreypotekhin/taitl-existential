package com.taitl.existential.builders;

import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Fluent builder for {@link Intent} rules.
 *
 * @param <T>
 *            Subject type the intents target
 */
public class IntentBuilder<T> implements EvsBuilder<T>
{
    ContextBuilder parent;
    TransactionBuilder parent2;
    Intent<T> target;

    public IntentBuilder(ContextBuilder parent)
    {
        sane(parent, "parent");
        throw new IllegalStateException("IntentBuilder requires a TypeKey. Use ContextBuilder.intent(Class<T>) "
                + "or ContextBuilder.intent(TypeKey<T>).");
    }

    public IntentBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = new Intent<>(typeKey);
    }

    public IntentBuilder(TransactionBuilder parent2)
    {
        sane(parent2, "parent2");
        throw new IllegalStateException("IntentBuilder requires a TypeKey. Use TransactionBuilder.intent(Class<T>) "
                + "or TransactionBuilder.intent(TypeKey<T>).");
    }

    public IntentBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = new Intent<>(typeKey);
    }

    public IntentBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        target.typeKey(typeKey);
        return this;
    }

    public IntentBuilder<T> on()
    {
        target.on();
        return this;
    }

    public IntentBuilder<T> on(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.on(condition);
        return this;
    }

    public IntentBuilder<T> on(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.on(condition, description);
        return this;
    }

    public IntentBuilder<T> create()
    {
        target.create();
        return this;
    }

    public IntentBuilder<T> create(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.create(condition);
        return this;
    }

    public IntentBuilder<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.create(condition, description);
        return this;
    }

    public IntentBuilder<T> change()
    {
        target.change();
        return this;
    }

    public IntentBuilder<T> change(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.change(condition);
        return this;
    }

    public IntentBuilder<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.change(condition, description);
        return this;
    }

    public IntentBuilder<T> delete()
    {
        target.delete();
        return this;
    }

    public IntentBuilder<T> delete(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.delete(condition);
        return this;
    }

    public IntentBuilder<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.delete(condition, description);
        return this;
    }

    public IntentBuilder<T> modify()
    {
        target.modify();
        return this;
    }

    public IntentBuilder<T> modify(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.modify(condition);
        return this;
    }

    public IntentBuilder<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.modify(condition, description);
        return this;
    }

    public IntentBuilder<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public IntentBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public IntentBuilder<T> read()
    {
        target.read();
        return this;
    }

    public IntentBuilder<T> read(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.read(condition);
        return this;
    }

    public IntentBuilder<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.read(condition, description);
        return this;
    }

    public IntentBuilder<T> readAndLock()
    {
        target.readAndLock();
        return this;
    }

    public IntentBuilder<T> readAndLock(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.readAndLock(condition);
        return this;
    }

    public IntentBuilder<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.readAndLock(condition, description);
        return this;
    }

    public IntentBuilder<T> write()
    {
        target.write();
        return this;
    }

    public IntentBuilder<T> write(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.write(condition);
        return this;
    }

    public IntentBuilder<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.write(condition, description);
        return this;
    }

    public IntentBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public IntentBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public IntentBuilder<T> update()
    {
        target.update();
        return this;
    }

    public IntentBuilder<T> update(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.update(condition);
        return this;
    }

    public IntentBuilder<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.update(condition, description);
        return this;
    }

    public IntentBuilder<T> upsert()
    {
        target.upsert();
        return this;
    }

    public IntentBuilder<T> upsert(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.upsert(condition);
        return this;
    }

    public IntentBuilder<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.upsert(condition, description);
        return this;
    }

    public Intent<T> build()
    {
        return target;
    }

    public ContextBuilder done()
    {
        return parent;
    }

    public TransactionBuilder doneTran()
    {
        return parent2;
    }
}
