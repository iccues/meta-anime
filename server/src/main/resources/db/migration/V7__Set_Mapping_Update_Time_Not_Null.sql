-- 指标历史使用 update_time 作为采样时间，存量数据需满足非空约束。
ALTER TABLE mapping
    ALTER COLUMN update_time SET NOT NULL;
