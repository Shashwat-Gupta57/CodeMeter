# Story Engine Guidelines

This document serves as the permanent design contract for the CodeMeter Story Engine.
It is intended for future contributors to understand the architectural philosophy of the physical metrics engine.

## The Philosophy
CodeMeter's biggest differentiator is that it translates software into physical representations. Every one of those representations is based on assumptions. 
Those assumptions must **NEVER** be hidden. They must **NEVER** be hardcoded. They must **ALWAYS** be editable.

The user should always know: "Why is this number what it is?"
Every physical calculation should be reproducible. Every estimate should be transparent.

## New Engineering Rule
From CodeMeter v2.3 onwards, **EVERY** new Story metric, **EVERY** new comparison, **EVERY** new estimate, and **EVERY** new physical calculation must first ask:

> "Does this depend on an assumption?"

If the answer is **YES**, that assumption MUST become part of the global configuration.
This is a permanent development rule. No exceptions.

## Workflow for Modifying the Story Engine
Before adding ANY new Story section, you must adhere to the following workflow:

**Step 1:** Identify every assumption used (e.g., printer, bookshelf, ink, paper weight, human reading speed, distance, height, weight, printing cost, energy usage, CO₂).

**Step 2:** Check whether that assumption already exists inside `Settings.java` and `config.toml`.

**Step 3:** If it does not exist, add it.

**Step 4:** Only then implement the calculation inside `PhysicalCalculator` using the configured values.

- Never hardcode physical values.
- Never hardcode estimated values.
- Never introduce "magic numbers".

If a Story section depends on an assumption, the user owns that assumption.

## Confidence & Transparency
Every Story value output must identify whether it is:
- **Measured** (derived directly from scan results)
- **Calculated** (derived mathematically using physical assumptions)
- **Estimated** (an approximation based on broad assumptions)
- **Configured** (a raw setting value)

Example:
> Printing Cost: $487
> (Estimated using $0.05 per printed page)

The Story Engine should never contain hidden constants. Every physical assumption belongs to the user. Every estimate should be explainable. Every calculation should be reproducible. That transparency is one of CodeMeter's defining features.
