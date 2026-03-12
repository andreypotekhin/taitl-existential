package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Builds {@link Intent} rules.
 *
 * @param <T> Type to apply the intents
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
        target.requireDescriptions(parent.parent.requireBehaviorDescriptions());
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
        target.requireDescriptions(parent2.parent.parent.requireBehaviorDescriptions());
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
        sane(condition, "condition");
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
        sane(condition, "condition");
        target.create(condition, description);
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
        sane(condition, "condition");
        target.delete(condition, description);
        return this;
    }

    public IntentBuilder<T> transit()
    {
        target.transit();
        return this;
    }

    public IntentBuilder<T> transit(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.transit(condition);
        return this;
    }

    public IntentBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        target.transit(condition, description);
        return this;
    }

    public IntentBuilder<T> transit(BiPredicate<? super T, ? super T> condition)
    {
        sane(condition, "condition");
        target.transit(condition);
        return this;
    }

    public IntentBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        target.transit(condition, description);
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
        sane(condition, "condition");
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
        sane(condition, "condition");
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
        sane(condition, "condition");
        target.write(condition, description);
        return this;
    }

    public IntentBuilder<T> port()
    {
        target.port();
        return this;
    }

    public IntentBuilder<T> port(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.port(condition);
        return this;
    }

    public IntentBuilder<T> port(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        target.port(condition, description);
        return this;
    }

    public IntentBuilder<T> port(BiPredicate<? super T, ? super T> condition)
    {
        sane(condition, "condition");
        target.port(condition);
        return this;
    }

    public IntentBuilder<T> port(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        target.port(condition, description);
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
        sane(condition, "condition");
        target.update(condition, description);
        return this;
    }

    public IntentBuilder<T> cu()
    {
        target.cu();
        return this;
    }

    public IntentBuilder<T> cu(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.cu(condition);
        return this;
    }

    public IntentBuilder<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        target.cu(condition, description);
        return this;
    }

    public IntentBuilder<T> cud()
    {
        target.cud();
        return this;
    }

    public IntentBuilder<T> cud(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.cud(condition);
        return this;
    }

    public IntentBuilder<T> cud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        target.cud(condition, description);
        return this;
    }

    public IntentBuilder<T> ud()
    {
        target.ud();
        return this;
    }

    public IntentBuilder<T> ud(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        target.ud(condition);
        return this;
    }

    public IntentBuilder<T> ud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        target.ud(condition, description);
        return this;
    }

    public Intent<T> build()
    {
        return target;
    }

    public <U> InvariantBuilder<U> invariant(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.invariant(cls);
        }
        return parent2.invariant(cls);
    }

    public <U> InvariantBuilder<U> invariant(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.invariant(typeKey);
        }
        return parent2.invariant(typeKey);
    }

    public <U> ContextBuilder invariant(Invariant<U> invariant)
    {
        sane(invariant, "invariant");
        return parentContext().invariant(invariant);
    }

    public <U> EffectBuilder<U> effect(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.effect(cls);
        }
        return parent2.effect(cls);
    }

    public <U> EffectBuilder<U> effect(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.effect(typeKey);
        }
        return parent2.effect(typeKey);
    }

    public <U> ContextBuilder effect(Effect<U> effect)
    {
        sane(effect, "effect");
        return parentContext().effect(effect);
    }

    public <U> IntentBuilder<U> intent(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.intent(cls);
        }
        return parent2.intent(cls);
    }

    public <U> IntentBuilder<U> intent(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.intent(typeKey);
        }
        return parent2.intent(typeKey);
    }

    public <U> ContextBuilder intent(Intent<U> intent)
    {
        sane(intent, "intent");
        return parentContext().intent(intent);
    }

    public ContextBuilder begin()
    {
        return parentContext().begin();
    }

    public ContextBuilder commit()
    {
        return parentContext().commit();
    }

    public ContextBuilder checkpoint()
    {
        return parentContext().checkpoint();
    }

    public ContextBuilder rollback()
    {
        return parentContext().rollback();
    }

    public ContextBuilder immediate()
    {
        return parentContext().immediate();
    }

    public ContextBuilder validation()
    {
        return parentContext().validation();
    }

    public TransactionBuilder transaction(String name)
    {
        sane(name, "name");
        return parentContext().transaction(name);
    }

    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().transaction(supplier);
    }

    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().contextFactory(supplier);
    }

    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (parent != null)
        {
            return parent.context(name);
        }
        return parent2.context(name);
    }

    public ContextBuilder context()
    {
        if (parent != null)
        {
            return parent.context();
        }
        return parent2.contextBuilder();
    }

    public TransactionBuilder doneTran()
    {
        return parent2;
    }

    protected ContextBuilder parentContext()
    {
        if (parent != null)
        {
            return parent;
        }
        return parent2.contextBuilder();
    }
}
