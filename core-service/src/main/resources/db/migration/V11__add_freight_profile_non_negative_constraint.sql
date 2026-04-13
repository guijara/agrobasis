ALTER TABLE freight_profile
    ADD CONSTRAINT ck_freight_profile_freight_per_ton_non_negative
        CHECK (freight_per_ton >= 0);
