package com.taitl.existential.builders;

import com.taitl.ex.common.creator.*;
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

    /**
     * Creates an intent builder for a context or transaction.
     *
     * @param parent
     *            Parent builder that owns this configuration.
     */
    public IntentBuilder(ContextBuilder parent)
    {
        sane(parent, "parent");
        throw new IllegalStateException("IntentBuilder requires a TypeKey. Use ContextBuilder.intent(Class<T>) "
                + "or ContextBuilder.intent(TypeKey<T>).");
    }

    /**
     * Creates an intent builder for a context or transaction.
     *
     * @param parent
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public IntentBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = Creator.create(Intent.class, new Class<?>[] { TypeKey.class }, typeKey);
    }

    /**
     * Creates an intent builder for a context or transaction.
     *
     * @param parent2
     *            Parent builder that owns this configuration.
     */
    public IntentBuilder(TransactionBuilder parent2)
    {
        sane(parent2, "parent2");
        throw new IllegalStateException("IntentBuilder requires a TypeKey. Use TransactionBuilder.intent(Class<T>) "
                + "or TransactionBuilder.intent(TypeKey<T>).");
    }

    /**
     * Creates an intent builder for a context or transaction.
     *
     * @param parent2
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public IntentBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = Creator.create(Intent.class, new Class<?>[] { TypeKey.class }, typeKey);
    }

    /**
     * Sets the type key for this rule.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        checkBuilder();
        target.typeKey(typeKey);
        return this;
    }

    /**
     * Declares intent for the On event, which can represent any action.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> on()
    {
        checkBuilder();
        target.on();
        return this;
    }

    /**
     * Declares intent for the On event, which can represent any action.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> on(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.on(condition);
        return this;
    }

    /**
     * Declares intent for the On event, which can represent any action.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> on(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.on(condition, description);
        return this;
    }

    /**
     * Adds an intent for create events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> create()
    {
        checkBuilder();
        target.create();
        return this;
    }

    /**
     * Adds an intent for create events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> create(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.create(condition);
        return this;
    }

    /**
     * Adds an intent for create events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.create(condition, description);
        return this;
    }

    /**
     * Adds an intent for delete events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> delete()
    {
        checkBuilder();
        target.delete();
        return this;
    }

    /**
     * Adds an intent for delete events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> delete(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.delete(condition);
        return this;
    }

    /**
     * Adds an intent for delete events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.delete(condition, description);
        return this;
    }

    /**
     * Adds an intent for transit events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> transit()
    {
        checkBuilder();
        target.transit();
        return this;
    }

    /**
     * Adds an intent for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> transit(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition);
        return this;
    }

    /**
     * Adds an intent for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds an intent for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> transit(BiPredicate<? super T, ? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition);
        return this;
    }

    /**
     * Adds an intent for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds an intent for read events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> read()
    {
        checkBuilder();
        target.read();
        return this;
    }

    /**
     * Adds an intent for read events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> read(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.read(condition);
        return this;
    }

    /**
     * Adds an intent for read events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.read(condition, description);
        return this;
    }

    /**
     * Adds an intent for rl events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> rl()
    {
        checkBuilder();
        target.rl();
        return this;
    }

    /**
     * Adds an intent for rl events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> rl(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.rl(condition, description);
        return this;
    }

    /**
     * Adds an intent for rl events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> rl(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.rl(condition);
        return this;
    }

    /**
     * Adds an intent for write events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> write()
    {
        checkBuilder();
        target.write();
        return this;
    }

    /**
     * Adds an intent for write events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> write(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.write(condition);
        return this;
    }

    /**
     * Adds an intent for write events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.write(condition, description);
        return this;
    }

    /**
     * Adds an intent for port events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> port()
    {
        checkBuilder();
        target.port();
        return this;
    }

    /**
     * Adds an intent for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> port(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition);
        return this;
    }

    /**
     * Adds an intent for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> port(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition, description);
        return this;
    }

    /**
     * Adds an intent for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> port(BiPredicate<? super T, ? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition);
        return this;
    }

    /**
     * Adds an intent for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> port(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition, description);
        return this;
    }

    /**
     * Adds an intent for update events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> update()
    {
        checkBuilder();
        target.update();
        return this;
    }

    /**
     * Adds an intent for update events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> update(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.update(condition);
        return this;
    }

    /**
     * Adds an intent for update events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.update(condition, description);
        return this;
    }

    /**
     * Adds an intent for cu events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cu()
    {
        checkBuilder();
        target.cu();
        return this;
    }

    /**
     * Adds an intent for cu events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cu(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cu(condition);
        return this;
    }

    /**
     * Adds an intent for cu events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cu(condition, description);
        return this;
    }

    /**
     * Adds an intent for cud events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cud()
    {
        checkBuilder();
        target.cud();
        return this;
    }

    /**
     * Adds an intent for cud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cud(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cud(condition);
        return this;
    }

    /**
     * Adds an intent for cud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> cud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cud(condition, description);
        return this;
    }

    /**
     * Adds an intent for ud events.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> ud()
    {
        checkBuilder();
        target.ud();
        return this;
    }

    /**
     * Adds an intent for ud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> ud(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        checkBuilder();
        target.ud(condition);
        return this;
    }

    /**
     * Adds an intent for ud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public IntentBuilder<T> ud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.ud(condition, description);
        return this;
    }

    /**
     * Builds the configured intent.
     * @return
     *            The configured rule.
     */
    public Intent<T> build()
    {
        return target;
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> InvariantBuilder<U> invariant(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.invariant(cls);
        }
        return parent2.invariant(cls);
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> InvariantBuilder<U> invariant(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.invariant(typeKey);
        }
        return parent2.invariant(typeKey);
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param invariant
     *            Invariant to add or configure.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> ContextBuilder invariant(Invariant<U> invariant)
    {
        sane(invariant, "invariant");
        return parentContext().invariant(invariant);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> EffectBuilder<U> effect(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.effect(cls);
        }
        return parent2.effect(cls);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> EffectBuilder<U> effect(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.effect(typeKey);
        }
        return parent2.effect(typeKey);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param effect
     *            Effect to add or configure.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> ContextBuilder effect(Effect<U> effect)
    {
        sane(effect, "effect");
        return parentContext().effect(effect);
    }

    /**
     * Starts or adds an intent rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> IntentBuilder<U> intent(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.intent(cls);
        }
        return parent2.intent(cls);
    }

    /**
     * Starts or adds an intent rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> IntentBuilder<U> intent(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.intent(typeKey);
        }
        return parent2.intent(typeKey);
    }

    /**
     * Registers an already-built sibling intent on the parent context.
     *
     * @param intent
     *            Intent to register
     * @param typeKey
     *            Type key for the intent subject
     * @param <U>
     *            Subject type for the intent
     * @return Parent context builder for chaining
     */
    public <U> ContextBuilder intent(Intent<U> intent, TypeKey<U> typeKey)
    {
        sane(intent, "intent", typeKey, "typeKey");
        return parentContext().intent(intent, typeKey);
    }

    /**
     * Sets begin as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder begin()
    {
        return parentContext().begin();
    }

    /**
     * Sets commit as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder commit()
    {
        return parentContext().commit();
    }

    /**
     * Sets checkpoint as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder checkpoint()
    {
        return parentContext().checkpoint();
    }

    /**
     * Sets rollback as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder rollback()
    {
        return parentContext().rollback();
    }

    /**
     * Sets immediate as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder immediate()
    {
        return parentContext().immediate();
    }

    /**
     * Sets validation as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder validation()
    {
        return parentContext().validation();
    }

    /**
     * Starts configuring a transaction for this operation.
     *
     * @param name
     *            Business operation name.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder transaction(String name)
    {
        sane(name, "name");
        return parentContext().transaction(name);
    }

    /**
     * Starts configuring a transaction for this operation.
     *
     * @param supplier
     *            Factory used to create the configured value.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().transaction(supplier);
    }

    /**
     * Sets the factory used to create contexts.
     *
     * @param supplier
     *            Factory used to create the configured value.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().contextFactory(supplier);
    }

    /**
     * Adds or selects a context for this configuration.
     *
     * @param name
     *            Business operation name.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (parent != null)
        {
            return parent.context(name);
        }
        return parent2.context(name);
    }

    /**
     * Adds or selects a context for this configuration.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder context()
    {
        if (parent != null)
        {
            return parent.context();
        }
        return parent2.contextBuilder();
    }

    protected void checkBuilder()
    {
        if (parent != null)
        {
            parent.parent.verifyConfigurable();
            target.requireDescriptions(parent.parent.requireBehaviorDescriptions());
        }
        else
        {
            parent2.parent.parent.verifyConfigurable();
            target.requireDescriptions(parent2.parent.parent.requireBehaviorDescriptions());
        }
    }

    /**
     * Returns to the parent transaction builder.
     * @return
     *            This builder for chaining.
     */
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
