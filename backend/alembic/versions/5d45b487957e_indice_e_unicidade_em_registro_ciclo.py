"""indice e unicidade em registro_ciclo

Revision ID: 5d45b487957e
Revises: 0267a59d2ee4
Create Date: 2026-07-27 19:53:21.398398

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '5d45b487957e'
down_revision: Union[str, Sequence[str], None] = '0267a59d2ee4'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    with op.batch_alter_table('registro_ciclo') as batch_op:
        batch_op.create_index(
            op.f('ix_registro_ciclo_usuario_id'), ['usuario_id'], unique=False
        )
        batch_op.create_unique_constraint(
            'uq_registro_ciclo_usuario_data', ['usuario_id', 'data']
        )


def downgrade() -> None:
    """Downgrade schema."""
    with op.batch_alter_table('registro_ciclo') as batch_op:
        batch_op.drop_constraint('uq_registro_ciclo_usuario_data', type_='unique')
        batch_op.drop_index(op.f('ix_registro_ciclo_usuario_id'))
