CREATE TABLE crop (
      id UUID PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      product VARCHAR(100) NOT NULL,
      start_date DATE NOT NULL,
      end_date DATE NOT NULL,
      plot_id UUID NOT NULL,

      CONSTRAINT fk_crop_plot
          FOREIGN KEY (plot_id)
              REFERENCES plots (id)
              ON DELETE CASCADE
);

CREATE INDEX idx_crop_plot_id ON crop(plot_id);