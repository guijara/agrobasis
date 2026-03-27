
CREATE TABLE plots (
       id UUID PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       hectare_area NUMERIC(10, 2) NOT NULL,
       farm_id UUID NOT NULL,

       CONSTRAINT fk_plot_farm
           FOREIGN KEY (farm_id)
               REFERENCES farm (id)
               ON DELETE CASCADE
);

CREATE INDEX idx_plot_farm_id ON plots(farm_id);