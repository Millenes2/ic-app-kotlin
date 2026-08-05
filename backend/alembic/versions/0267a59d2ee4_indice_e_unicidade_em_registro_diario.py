"""indice e unicidade em registro_diario

Revision ID: 0267a59d2ee4
Revises: d3396c157df2
Create Date: 2026-07-27 19:23:17.080621

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '0267a59d2ee4'
down_revision: Union[str, Sequence[str], None] = 'd3396c157df2'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    with op.batch_alter_table('registro_diario') as batch_op:
        batch_op.create_index(
            op.f('ix_registro_diario_usuario_id'), ['usuario_id'], unique=False
        )
        batch_op.create_unique_constraint(
            'uq_registro_diario_usuario_data', ['usuario_id', 'data']
        )


def downgrade() -> None:
    """Downgrade schema."""
    with op.batch_alter_table('registro_diario') as batch_op:
        batch_op.drop_constraint('uq_registro_diario_usuario_data', type_='unique')
        batch_op.drop_index(op.f('ix_registro_diario_usuario_id'))
